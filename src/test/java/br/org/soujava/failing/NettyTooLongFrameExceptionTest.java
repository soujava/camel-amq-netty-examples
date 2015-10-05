package br.org.soujava.failing;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * This test case exposes the TooLongFrameException when netty sends a message from point A to point B
 *
 */
public class NettyTooLongFrameExceptionTest extends BaseNettyTest {

    private String uri = "netty:tcp://localhost:{{port}}";
    private int clientCount = 1;
    private CountDownLatch finishLatch = new CountDownLatch(clientCount);

    @Test(expected = CamelExecutionException.class )
    public void testShouldFailWhenMessageIsTooBig() throws Exception {
        String msg =
                "<Data>" +
                "  <Node>" +
                "    <Element Type=\"Sou\">Bam Bam!</Element>" +
                "  </Node>" +
                "  <Node>" +
                "    <Element Type=\"Java\">Rock it in!</Element>" +
                "  </Node>" +
                "  <Node>" +
                "    <Element Type=\"Rulez\">Lets grab a beer!</Element>" +
                "  </Node>" +
                "</Data>";

        for (;msg.length() < 1445477;){
            msg = msg + msg;
        }

        final String xml = msg;
        File file = new File("/tmp/netty/messageTooBig.txt");
        // save to file, do not append
        FileOutputStream fos = new FileOutputStream(file, false);
        try {
            fos.write(String.valueOf(xml).getBytes());
        } finally {
            fos.close();
        }


        Object res = template.requestBody(uri, xml);
        finishLatch.countDown();

        getMockEndpoint("mock:result").expectedMessageCount(clientCount);

        // and wait long enough until they're all done
        assertTrue("Waiting on the latch ended up with a timeout!", finishLatch.await(5, TimeUnit.SECONDS));

        // assert on what we expect to receive
        for (int i = 0; i < clientCount; i++) {
            assertEquals("SouJava"+xml, res);
        }
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(uri)
                        .log("${body}")
                        .transform(body().prepend("SouJava"))
                        .to("mock:result");
            }
        };
    }
}
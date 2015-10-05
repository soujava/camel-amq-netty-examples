package br.org.soujava.failing;

import br.org.soujava.schema.webservice.wsdl._1_1.EsbWebService;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * This test case exposes the Netty TooLongFrameException when a SOAP message is send from CXF endpoint to Netty via TCP
 */
public class CXFNettyClientSpringTest extends CamelSpringTestSupport {

    // should be the same address as we have in our route
    private static final String URL = "http://localhost:9998/esbWebServiceEndpoint";


    protected static EsbWebService createCXFClient(String url) {
        // we use CXF to create a client for us as its easier than JAXWS and works
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EsbWebService.class);
        factory.setAddress(url);
        return (EsbWebService) factory.create();
    }

    @Test
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

        System.out.println(xml);

        // create the webservice client and send the request
        EsbWebService client = createCXFClient(URL);
        Object o = client.postEsbMessage(xml);
        System.out.println(o);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext(new String[] {"camel-context.xml"});
    }


}
package org.wso2.xml;

import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;


public class TestXPath extends AXIOMXPath {

    private static final long serialVersionUID = 763922613753433423L;

    private String expression;

    public TestXPath(String expression) throws JaxenException {
        super(expression);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(Object context) throws JaxenException {
        return super.evaluate(context);
    }

}

/*
 * Copyright (C) 2013 salesforce.com, inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.auraframework.integration.test.root.parser.handler.design;

import org.auraframework.Aura;
import org.auraframework.def.ComponentDef;
import org.auraframework.def.DefDescriptor;
import org.auraframework.def.design.DesignAttributeDef;
import org.auraframework.def.design.DesignDef;
import org.auraframework.impl.AuraImplTestCase;
import org.auraframework.test.source.StringSourceLoader;
import org.auraframework.throwable.quickfix.InvalidDefinitionException;

public class DesignAttributeDefHandlerTest extends AuraImplTestCase {

    public DesignAttributeDefHandlerTest(String name) {
        super(name);
    }

    public void testGetElement() throws Exception {
        String name = "mystring";
        DesignAttributeDef element = setupAttributeDesignDef(
                name,
                "<design:attribute name=\""
                        + name
                        + "\" required=\"true\" readonly=\"true\" type=\"String\" dependsOn=\"myparent\" datasource=\"one,two,three\" min=\"-100\" max=\"100\" label=\"some label\" placeholder=\"some placeholder\" />");

        assertTrue(element.isRequired());
        assertTrue(element.isReadOnly());
        assertEquals("String", element.getType());
        assertEquals("myparent", element.getDependsOn());
        assertEquals("one,two,three", element.getDataSource());
        assertEquals("-100", element.getMin());
        assertEquals("100", element.getMax());
        assertEquals("some label", element.getLabel());
        assertEquals("some placeholder", element.getPlaceholderText());
    }

    public void testRequiredAndReadOnlyAttributeParsingNull() throws Exception {
        String name = "mystring";
        DesignAttributeDef element = setupAttributeDesignDef(name, "<design:attribute name=\"" + name + "\" />");
        assertFalse(element.isRequired());
        assertFalse(element.isReadOnly());
    }

    public void testRequiredAndReadOnlyAttributeParsingNotNull() throws Exception {
        String name = "mystring";
        DesignAttributeDef element = setupAttributeDesignDef(name,
                "<design:attribute name=\"" + name + "\" required=\"nottrue\" readonly=\"nottrue\" />");
        assertFalse(element.isRequired());
        assertFalse(element.isReadOnly());
        element = setupAttributeDesignDef(name, "<design:attribute name=\"" + name
                + "\" required=\"\" readonly=\"\" />");
        assertFalse(element.isRequired());
        assertFalse(element.isReadOnly());
    }

    public void testInvalidSystemAttributeName() throws Exception {
        try {
            String name = "mystring";
            setupAttributeDesignDef(name, "<design:attribute name=\"" + name + "\" foo=\"bar\" />");
            fail("Expected InvalidDefinitionException to be thrown");
        } catch (Exception t) {
            assertExceptionMessageEndsWith(t, InvalidDefinitionException.class, "Invalid attribute \"foo\"");
        }
    }

    public void testInvalidSystemAttributePrefix() throws Exception {
        try {
            String name = "mystring";
            setupAttributeDesignDef(name, "<design:attribute name=\"" + name + "\" other:required=\"false\" />");
            fail("Expected InvalidDefinitionException to be thrown");
        } catch (Exception t) {
            assertExceptionMessageEndsWith(t, InvalidDefinitionException.class,
                    "Invalid attribute \"other:required\"");
        }
    }

    public void testDesignFileWithInvalidAttributeTypesExposed() throws Exception {
        String cmp = "<aura:attribute name=\"invalidAttribute\" type=\"String[]\" />";
        String design = "<design:component><design:attribute name=\"invalidAttribute\" /> </design:component>";
        DefDescriptor<ComponentDef> cmpDesc = createAuraDefinitionWithDesignFile(cmp, design);

        try {
            cmpDesc.getDef();
            fail("String[] attribute should not be allowed to be exposed in the design file.");
        } catch (Exception t) {
            assertExceptionMessageStartsWith(t, InvalidDefinitionException.class,
                    "Only Boolean, Integer or String attributes may be exposed in design files.");
        }
    }

    public void testDesignFileWithInvalidAttributeTypeForDataSource() throws Exception {
        String cmp = "<aura:attribute name=\"invalidAttribute\" type=\"Integer\" />";
        String design = "<design:component><design:attribute name=\"invalidAttribute\" datasource=\"1,2,3\" /> </design:component>";
        DefDescriptor<ComponentDef> cmpDesc = createAuraDefinitionWithDesignFile(cmp, design);

        try {
            Aura.getDefinitionService().getDefinition(cmpDesc.getQualifiedName(), ComponentDef.class);
            fail("Integer attribute should not allow to have a Datasource");
        } catch (Exception t) {
            assertExceptionMessageStartsWith(t, InvalidDefinitionException.class,
                    "Only String attributes may have a datasource in the design file.");
        }
    }

    public void testDesignFileWithDuplicateAttribute() throws Exception {
        final String attr = "attr";
        String cmp = "<aura:attribute name=\"" + attr +"\" type=\"String\" />";
        String design = "<design:component>" +
                "<design:attribute name=\"" + attr + "\"/>" +
                "<design:attribute name=\"" + attr + "\"/> </design:component>";
        DefDescriptor<ComponentDef> cmpDesc = createAuraDefinitionWithDesignFile(cmp, design);

        try {
            Aura.getDefinitionService().getDefinition(cmpDesc.getQualifiedName(), ComponentDef.class);
            fail("Design file should prevent duplicate attributes from being saved");
        } catch (Exception t) {
            assertExceptionMessageStartsWith(t, InvalidDefinitionException.class,
                    String.format("Design attribute %s already defined", attr));
        }
    }


    private DesignAttributeDef setupAttributeDesignDef(String name, String markup) throws Exception {
        DefDescriptor<ComponentDef> cmpDesc = getAuraTestingUtil().createStringSourceDescriptor(null,
                ComponentDef.class, null);
        String cmpBody = "<aura:attribute name='" + name + "' type='String' />";
        addSourceAutoCleanup(cmpDesc, String.format(baseComponentTag, "", cmpBody));

        DefDescriptor<DesignDef> designDesc = Aura.getDefinitionService().getDefDescriptor(cmpDesc.getQualifiedName(),
                DesignDef.class);
        addSourceAutoCleanup(designDesc, String.format("<design:component>%s</design:component>", markup));

        return designDesc.getDef().getAttributeDesignDef(name);
    }

    private DefDescriptor<ComponentDef> createAuraDefinitionWithDesignFile(String cmpAttributes, String designSource) {
        DefDescriptor<ComponentDef> cmpDesc = getAuraTestingUtil().createStringSourceDescriptor(StringSourceLoader.DEFAULT_CUSTOM_NAMESPACE + ":",
                ComponentDef.class, null);
        getAuraTestingUtil().addSourceAutoCleanup(cmpDesc, String.format(baseComponentTag, "", cmpAttributes), false);
        DefDescriptor<DesignDef> desc = Aura.getDefinitionService().getDefDescriptor(cmpDesc.getQualifiedName(),
                DesignDef.class);
        getAuraTestingUtil().addSourceAutoCleanup(desc, designSource, false);
        return cmpDesc;
    }
}

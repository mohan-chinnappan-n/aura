({
    /**
     * Note that this test file operates in system mode (objects are not Lockerized) so the tests delegate logic and
     * verification to the controller and helper files, which operate in user mode.
     */

    // LockerService not supported on older IE
    browsers: ["-IE8", "-IE9", "-IE10"],

    setUp: function(cmp) {
        cmp.set("v.testUtils", $A.test);
    },

    testCanAccessDocumentBodyFromHelper: {
        test: function(cmp) {
            cmp.helper.testCanAccessDocumentBodyFromHelper($A.test);
        }
    },

    testCanAccessDocumentHeadFromHelper: {
        test: function(cmp) {
            cmp.helper.testCanAccessDocumentHeadFromHelper($A.test);
        }
    },

    testAuraLockerInController: {
        test: function(cmp) {
            cmp.testAuraLockerInController();
        }
    },

    testAuraLockerInHelper: {
        test: function(cmp) {
            cmp.helper.testAuraLockerInHelper($A.test);
        }
    },

    testSecureWrappersInRenderer: {
        attributes: {
            testRenderer: true
        },
        test: function(cmp) {
            // Renderer will throw an error on load if anything is not Lockerized as expected, nothing to assert here.
        }
    },

    testComponentLockerInController: {
        test: function(cmp) {
            cmp.testComponentLockerInController();
        }
    },

    testDocumentLockerInController: {
        test: function(cmp) {
            cmp.testDocumentLockerInController();
        }
    },

    testDocumentLockerInHelper: {
        test: function(cmp) {
            cmp.helper.testDocumentLockerInHelper($A.test);
        }
    },

    testWindowLockerInController: {
        test: function(cmp) {
            cmp.testWindowLockerInController();
        }
    },

    testWindowLockerInHelper: {
        test: function(cmp) {
            cmp.helper.testWindowLockerInHelper($A.test);
        }
    },

    testAppendDynamicallyCreatedDivToMarkup: {
        test: function(cmp) {
            cmp.testAppendDynamicallyCreatedDivToMarkup();
        }
    },

    testContextOfController: {
        test: function(cmp) {
            cmp.testContextOfController();
        }
    },

    testDefineGetterExploit: {
        // This exploit not covered in IE11
        browsers: ["-IE8", "-IE9", "-IE10", "-IE11"],
        // Remove UnAdaptableTest label when unsafe-eval and unsafe-inline are added back to CSP
        labels: ["UnAdaptableTest"],
        test: function(cmp) {
            cmp.testDefineGetterExploit();
        }
    },

    /**
     * See W-2974202 for original exploit.
     */
    testSetTimeoutNonFunctionParamExploit: {
        test: function(cmp) {
            cmp.testSetTimeoutNonFunctionParamExploit();
        }
    },

    testComponentUnfilteredFromUserToSystemMode: {
        test: function(cmp) {
            cmp.testComponentUnfilteredFromUserToSystemMode();
            var component = cmp.get("v.componentStore");
            // Component should be unfiltered when returned to system mode
            $A.test.assertStartsWith("markup://lockerTest:facet", component.toString());
        }
    },

    testLocationExposed: {
        test: function(cmp) {
            cmp.testLocationExposed();
        }
    },

    testAttemptToEvalToWindow: {
        // This exploit not covered in IE11
        browsers: ["-IE8", "-IE9", "-IE10", "-IE11"],
        test: function(cmp) {
            cmp.testEvalBlocking();

            // DCHASMAN TOOD Port these to cmp.testEvalBlocking()

            // eval attempts that result in an error
            /*try {
                var symbol = "toString.constructor.prototype";
                cmp.testSymbol(symbol);
                $A.test.fail("eval'ing [" + symbol + "] should throw an error");
            } catch(e) {
                var error = e.toString();
                $A.test.assertStartsWith("TypeError: Cannot read property 'constructor' of undefined", error);
            }

            try {
                var symbol = "''.substring.call.call(({})[\"constructor\"].getOwnPropertyDescriptor(''.substring.__pro"
                    + "to__, \"constructor\").value, null, \"return this;\")()"
                cmp.testSymbol(symbol);
                $A.test.fail("eval'ing [" + symbol + "] should throw an error");
            } catch(e) {
                var error = e.toString();
                $A.test.assertStartsWith("Error: Security violation: use of __proto__", error);
            }*/
        }
    },

    testValueProviderOnDynamicallyCreatedComponents: {
        test: function(cmp) {
            cmp.testValueProviderOnDynamicallyCreatedComponents();
        }
    },

    testThisVariableNotLeakedFromMarkup: {
        test: function(cmp) {
            cmp.testThisVariableNotLeakedFromMarkup();
        }
    },
    
    testCtorAannotation: {
        test: function(cmp) {
            cmp.testCtorAannotation();
        }
    }
})

package jadx.tests.integration.conditions;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestConditions extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, not(containsString("(!a || !b) && !c")));
        assertThat(code, containsString("return (a && b) || c;"));
    }

    public static class TestCls {
        private boolean test(boolean a, boolean b, boolean c) {
            return (a && b) || c;
        }
    }
}

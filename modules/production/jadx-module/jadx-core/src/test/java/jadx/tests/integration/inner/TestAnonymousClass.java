package jadx.tests.integration.inner;

import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class TestAnonymousClass extends IntegrationTest {

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestCls.class);
        String code = cls.getCode().toString();

        assertThat(code, containsString("new File(\"a\").list(new FilenameFilter()"));
        assertThat(code, not(containsString("synthetic")));
        assertThat(code, not(containsString("this")));
        assertThat(code, not(containsString("null")));
        assertThat(code, not(containsString("AnonymousClass_")));
    }

    public static class TestCls {

        public int test() {
            String[] files = new File("a").list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.equals("a");
                }
            });
            return files.length;
        }
    }
}

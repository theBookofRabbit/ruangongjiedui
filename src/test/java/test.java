import cn.ruangong.jiedui.JarMain;
import org.junit.Test;
import java.io.UnsupportedEncodingException;

public class test {

    JarMain  a = new JarMain();
    @Test
    public void testsizeyunsuan() throws Exception {

        String b = a.sizeyunsuan(null,"10","100");
        if(b != null){
            return;
        }
        else{
            throw new Exception();
        }
    }
}

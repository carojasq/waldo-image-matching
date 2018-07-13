package imageprocessing;

import java.util.Map;

public class TestTemplateMatching {


    public static void main(String[] args) {
        TemplateMatching tp = new TemplateMatching(1, 0.5);
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        Map<String, String> results;
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/example1_redcar20.jpg");
        assert(results.get("match").equals("t"));
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/example1_redcar50.jpg");
        assert(results.get("match").equals("t"));
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/example1_redcar80.jpg");
        assert(results.get("match").equals("t"));
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/example1_redcar.jpg");
        assert(results.get("match").equals("t"));
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/example1_redcar_no_match.jpg");
        assert(results.get("match").equals("f"));
        results = tp.isSubImage( "src/test/images/example1_complete.jpg","src/test/images/no_match.jpg");
        assert(results.get("match").equals("f"));

    }
}
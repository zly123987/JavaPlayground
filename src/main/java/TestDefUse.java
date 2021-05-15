
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import soot.*;
import soot.jimple.Stmt;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;

public class TestDefUse extends SceneTransformer {

    static LinkedList<String> excludeList;
    static FileWriter outNameList = null;
    static FileWriter out = null;
    public static void setOptions(String path){
        List<String> argsList = new ArrayList<>();
        argsList.addAll(Arrays.asList("-w", "-cp", path, "-pp", "-process-dir",path)); //"-allow-phantom-refs"

        argsList.add("-p"); argsList.add("cg"); argsList.add("all-reachable:true");

        String[] args = argsList.toArray(new String[0]);

        Options.v().parse(args);
    }
    public static void main (String[] args) throws IOException {
        new TestDefUse().generation(args);
    }


    public void generation(String[] args) throws IOException {


        String mainclass = "Main";
//        String sourceDirectory = "/home/cc/workspace/maven/maven-artifact/target/jar/maven-artifact-4.0.0-alpha-1-SNAPSHOT.jar";
        String sourceDirectory = args[0];
        setOptions(sourceDirectory);
        //set classpath
        File dir = new File("graph");
        dir.mkdir();
        String filename = args[1];
        out = new FileWriter("graph/"+filename+".csv", false);
        outNameList = new FileWriter("graph/"+filename+"_NameList.csv", false);


        String javapath = System.getProperty("java.class.path");
        String jredir = System.getProperty("java.home")+"/lib/rt.jar";
        String path = javapath+File.pathSeparator+jredir+File.pathSeparator+sourceDirectory;
//        path  = "/home/cc/workspace/sootTest/target/classes";
//        Scene.v().setSootClassPath(path);

        //add an intra-procedural analysis phase to Soot
        TestDefUse analysis = new TestDefUse();
        if (!PackManager.v().hasPhase("wjtp.TestSootCallGraph"))
            PackManager.v().getPack("wjtp").add(new Transform("wjtp.TestSootCallGraph", analysis));
//        System.out.println(path);

        excludeJDKLibrary();

        //whole program analysis
        Options.v().set_whole_program(true);
        //load and set main class
        Options.v().set_app(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));

//        SootClass appclass = Scene.v().loadClassAndSupport(mainclass);
//        Scene.v().setMainClass(appclass);
//        Scene.v().extendSootClassPath("/home/cc/workspace/maven/maven-artifact/target/classes");
        System.out.println("Generating Call Graph for " + filename);

        Scene.v().loadNecessaryClasses();


        //enable call graph
//        enableCHACallGraph();
        enableSparkCallGraph();

        //start working
        PackManager.v().runPacks();
        soot.G.reset();
    }
    private static String methodsToString(SootMethod m){
        String mstring = m.toString();
        mstring = mstring.replace("<", "").replace(">", "");
        String[] mthds = mstring.split(" ");
        return mthds[0]+mthds[2];
    }
    private static void excludeJDKLibrary()
    {
        //exclude jdk classes
        Options.v().set_exclude(excludeList());
        //this option must be disabled for a sound call graph
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
    }
    private static void enableSparkCallGraph() {

        //Enable Spark
        HashMap<String,String> opt = new HashMap<String,String>();
        //opt.put("propagator","worklist");
        //opt.put("simple-edges-bidirectional","false");
        opt.put("on-fly-cg","true");
        //opt.put("set-impl","double");
        //opt.put("double-set-old","hybrid");
        //opt.put("double-set-new","hybrid");
//        opt.put("pre_jimplify", "true");
        opt.put("simulate-natives", "false");
        SparkTransformer.v().transform("",opt);
        PhaseOptions.v().setPhaseOption("cg.spark", "enabled:true");
    }

    private static void enableCHACallGraph() {
        CHATransformer.v().transform();
    }

    private static LinkedList<String> excludeList()
    {
        if(excludeList==null)
        {
            excludeList = new LinkedList<String> ();

            excludeList.add("java.");
            excludeList.add("javax.");
//            excludeList.add("sun.");
//            excludeList.add("sunw.");
//            excludeList.add("com.sun.");
//            excludeList.add("com.ibm.");
//            excludeList.add("com.apple.");
//            excludeList.add("apple.awt.");
            excludeList.add("jdk.internal.");
//            excludeList.add("org.xml.sax.");
        }
        return excludeList;
    }
    private String getSig(SootMethod m){
        String ret = m.getDeclaringClass()+":"+m.getName()+"(";
        for (Type t: m.getParameterTypes()) {
            String type = t.toString();
            if (t.toString().contains(".")) {
                String[] ty = t.toString().split("\\.");
                type = ty[ty.length-1];
                if (type.contains("$")){
                    ty = type.toString().split("\\$");
                    type = ty[ty.length-1];
                }
            }
            ret = ret+type+",";
        }
        ret = ret+")";
        return ret.replaceAll(",\\)$", ")").replaceAll("\\$\\d+", "");
    }
    private boolean isExcluded(String s){
        return excludeList.stream().filter(e->s.startsWith(e)).count()>0;
    }
    @Override
    protected void internalTransform(String phaseName,
                                     Map options) {

        int numOfEdges = 0;

        CallGraph callGraph = Scene.v().getCallGraph();
        for(SootClass sc : Scene.v().getClasses()) {
//            System.out.println(sc);
            for (SootMethod m : sc.getMethods()) {
//                System.out.println(m );
                UnitGraph ug= new ExceptionalUnitGraph(m.getActiveBody());
                SimpleLocalDefs du =  new SimpleLocalDefs(ug);
                System.out.println(m.getActiveBody().getLocals());
                for(Unit u: ug.getTails()){
//                    System.out.println(du.getDefsOf(,u));
                    ;
                }
            }
        }
    }
}
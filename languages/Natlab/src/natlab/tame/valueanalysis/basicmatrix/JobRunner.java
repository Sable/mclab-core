package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.BasicTamerTool;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

import java.util.HashMap;

/**
 * Created by sameer on 29/07/15.
 */
public class JobRunner {
    private static HashMap<String, String> dirToDriverMap = new HashMap<>();

    static {
        dirToDriverMap = new HashMap<String, String>();
        dirToDriverMap.put("adpt", "drv_adpt.m");
        dirToDriverMap.put("bbai", "drv_babai.m");
        dirToDriverMap.put("bubble", "drv_bubble.m");
        dirToDriverMap.put("clos", "drv_clos.m");
        dirToDriverMap.put("diff", "drv_diff.m");
        dirToDriverMap.put("crni", "drv_crni.m");
        dirToDriverMap.put("fdtd", "drv_fdtd.m");
        dirToDriverMap.put("fft", "drv_fft.m");
        dirToDriverMap.put("fiff", "drv_fiff.m");
        dirToDriverMap.put("matmul", "drv_matmul_p.m");
        dirToDriverMap.put("mbrt", "drv_mbrt.m");
        dirToDriverMap.put("mcpi", "drv_mcpi_p.m");
        dirToDriverMap.put("nb1d", "drv_nb1d.m");
        dirToDriverMap.put("nb3d", "drv_nb3d.m");
        dirToDriverMap.put("numprime", "drv_prime.m");
        dirToDriverMap.put("optstop", "drv_osp.m");
        dirToDriverMap.put("quadrature", "drv_quad.m");
        dirToDriverMap.put("capr", "drv_capr.m");
        dirToDriverMap.put("scra", "drv_scra.m");
        dirToDriverMap.put("spqr", "drv_spqr.m");
    }

    public static void main(String args[]) {
        System.out.println("Running Tamer for Benchmarks");
        String dir_root = "/home/sameer/interview/mclab/";
        for (String key : dirToDriverMap.keySet()) {
            System.out.println("Benchmark " + key);
            GenericFile gFile = GenericFile
                    .create(dir_root + key + "/" + dirToDriverMap.get(key));
            FileEnvironment env = new FileEnvironment(gFile);
            SimpleFunctionCollection callgraph = new SimpleFunctionCollection(env);
            BasicTamerTool.setDoIntOk(false);
            try {
                ValueAnalysis<AggrValue<BasicMatrixValue>> analysis = BasicTamerTool.analyze(args, env);
            }catch(Exception e ){
                System.out.println("Error while compiling benchmark  "+ key);
                //e.printStackTrace();
            }
        }
    }
}

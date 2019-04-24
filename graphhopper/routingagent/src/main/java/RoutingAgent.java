import com.graphhopper.reader.dem.ElevationProvider;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.routing.util.DataFlagEncoder;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.PathWrapper;
import com.graphhopper.ui.MiniGraphUI;
import com.graphhopper.util.PointList;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.details.PathDetail;
import com.graphhopper.routing.Path;
import com.graphhopper.Trip.Leg;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.details.PathDetail;
import java.util.*;


public class RoutingAgent {
    private ElevationProvider eleProvider = ElevationProvider.NOOP;
    public static void main(String[] args) {
        new RoutingAgent().start();
    }
        private void start(){

            GraphHopper hopper = new GraphHopperOSM().forDesktop();
            hopper.setCHEnabled(false);
            String osmFile = "/home/arminakvn/Google Drive/mapc_works/app/graphhopper/latest.osm.pbf";
            String graphFolder = "./graphhoper_graph4c";
            hopper.setGraphHopperLocation(graphFolder);
            hopper.setDataReaderFile(osmFile);
//            DataFlagEncoder df = new DataFlagEncoder();

            hopper.setEncodingManager(new EncodingManager("mapcrider2"));
            hopper.setElevation(true);
            String datareaderfile = hopper.getDataReaderFile();
//            hopper.;
            hopper.importOrLoad();
            Double latFrom = 42.355147;
            Double lonFrom = -71.061609;
            Double latTo = 42.398822;
            Double lonTo = -71.124351;
            List<String> pathDetails = new ArrayList<>();
//            PathDetail pd = new PathDetail("weight_value");
//            List<String> det_reqs = Collections.emptyList();
            pathDetails.add(Parameters.DETAILS.EDGE_ID);
            GHRequest req = new GHRequest(latFrom, lonFrom, latTo, lonTo).
                    setWeighting("fastest").
                    setVehicle("mapcrider2").
                    setLocale(Locale.US).
                    setPathDetails(pathDetails).
                    setAlgorithm(Parameters.Algorithms.ALT_ROUTE);

//            req.getPathDetails();
//            req.getPointHints();
            GHResponse rsp = hopper.route(req);

// iterate over every turn instruction
//        System.out.println("rsp   "+rsp);
// first check for errors
            if(rsp.hasErrors()) {
                // handle them!
                rsp.getErrors();
                return;
            }


            PathWrapper path = rsp.getBest();
            List<PathWrapper> allpath = rsp.getAll();
            InstructionList il = path.getInstructions();
            Map<String, List<PathDetail>> pdet = path.getPathDetails();
// points, distance in meters and time in millis of the full path
            PointList pointList = path.getPoints();

            System.out.println("datareaderfile"+datareaderfile + req.getPathDetails());
            double distance = path.getDistance();
            Map<String, List<PathDetail>> path_details = path.getPathDetails();
            for (int i = 0; i <path_details.size() ; i++) {

                System.out.println("details   "+pdet.get("edge_id"));
            }


            long timeInMs = path.getTime();
            double weight = path.getRouteWeight();
            long alltimes = allpath.get(1).getTime();
            List<Leg> tlegs = path.getLegs();
            System.out.println("distance   "+distance);
            System.out.println("timeInMs   "+timeInMs);
            System.out.println("route weight   "+weight);
            System.out.println("pdet   "+pdet.size());
            for (int i = 0; i <pdet.size() ; i++) {

                List<PathDetail> p = pdet.get(i);
                System.out.println("p   "+p);
            }
        }


}

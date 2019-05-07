
function current_layer() {
    var layers = require("josm/layers");
    return layers.activeLayer;
}
function read_the_datafile(){
	var source="/home/arminakvn/Google Drive/mapc_works/studyarea.osm";
    var format="osm";
    var jio = org.openstreetmap.josm.io;    
    var io = java.io;
    var jtools = org.openstreetmap.josm.tools;
    is = new io.FileInputStream(source);
    var st = jio.OsmReader.parseDataSet(is, null)
    return st;
    }
    
function main_fun(){
    var util = require("josm/util");
    var command = require("josm/command");
    var console = require("josm/scriptingconsole");
	var layername = "cambridgevidion.osm";
	var layers = require("josm/layers");
	var  dataset =  read_the_datafile();
	layer0 = josm.layers.addDataLayer(dataset);
	var cycleways = dataset.query("type:way")
		for (j = 0; j < cycleways.length; j++) {
			var way = cycleways[j];
			var strees_level_map = {};
			strees_level_map["setcount"] = 0;
			var highway = way.get("highway");
			var bicycle = way.get("bicycle");
			var cycleway = way.get("cycleway");
			var has_cycleway = way.has("cycleway");
			var cycleway_right= way.get("cycleway:right");
			var cycleway_left= way.get("cycleway:left");

			var cycleway_right_buffer= way.get("cycleway:right:buffer");
			var cycleway_left_buffer= way.get("cycleway:left:buffer");
			var cycleway_right_lane= way.get("cycleway:right:lane");
			var cycleway_left_lane= way.get("cycleway:left:lane");

			var lanes= way.get("lanes");
			
			
			var has_maxspeed = way.has("maxspeed");
			if (has_maxspeed){
				var maxspeed = way.tags.maxspeed;
				maxspeed = maxspeed.replace(/[^\d.-]/g, ''); 
			}

			
			var has_width = way.has("width");
			if (has_width){
				var _width = way.tags.width;
				_width = _width.replace(/[^\d.-]/g, ''); 
			}
			
			if (highway == "cycleway"){
				if (cycleway == "track" || cycleway_right == "track" || cycleway_left == "track" || cycleway_left == "lane" || cycleway_left == "opposite" || bicycle == "designated") {
					strees_level_map["level"] =  "low";
					var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
					strees_level_map["setcount"] = setcnt+1
					

				} 	
			} else {
				if (has_cycleway){
					if (cycleway == "track" || cycleway_right == "track" || cycleway_left == "track") {
						strees_level_map["level"] =  "low";
						var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
						strees_level_map["setcount"] = setcnt+1
					} 
					if (cycleway_right_buffer == "yes" || cycleway_left_buffer == "yes" || cycleway_right_lane == "exclusive" || cycleway_left_lane == "exclusive") {
						if (has_maxspeed){
							if ((Math.floor(maxspeed)) < 30 && Math.floor(lanes) <= 2){
							strees_level_map["level"] =  "low";
							var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
							strees_level_map["setcount"] = setcnt+1
							}
						} else if ((highway == "residential" || highway == "tertiary") && Math.floor(lanes) == 1 ) {
							strees_level_map["level"] =  "low";
							var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
							strees_level_map["setcount"] = setcnt+1
						}
		
					}
					
					
					if (cycleway == "lane" || cycleway_right == "lane" || cycleway_left == "lane") {
						var parking_lane = way.has("parking:lane");
						if (parking_lane){							
						} else {
							if ((Math.floor(lanes) <= 2) && (highway != "primary" || highway != "secondary" || highway != "tertiary")){
								if (has_maxspeed){
									if (Math.floor(maxspeed) < 25){
										strees_level_map["level"] =  "low";
										var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
										strees_level_map["setcount"] = setcnt+1
									}
								} else if (has_width){
									if (Math.floor(_width)>9){
										strees_level_map["level"] =  "low";
										var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
										strees_level_map["setcount"] = setcnt+1
									}
								}
							}
						}
		
					}
					if (cycleway == "shared_lane" || cycleway_right == "shared_lane" || cycleway_left == "shared_lane"){
						if ((Math.floor(lanes) <= 2) && (highway != "primary" || highway != "secondary" || highway != "tertiary")){
							if (has_maxspeed){
								if (Math.floor(maxspeed) < 25){
									strees_level_map["level"] =  "low";
									var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
									strees_level_map["setcount"] = setcnt+1
								}
							} else if (has_width){
								if (Math.floor(_width)>9){
									strees_level_map["level"] =  "low";
									var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
									strees_level_map["setcount"] = setcnt+1
								}
							}
							
						}
		
					}
				} else {
					console.println("no cycleway")
					if ((Math.floor(lanes) <= 2) && (highway == "residential" || highway == "unclassified")){
						if (has_maxspeed){
							if (Math.floor(maxspeed) <= 25){
								strees_level_map["level"] =  "low";
								var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
								strees_level_map["setcount"] = setcnt+1
							}
						} else if (has_width){
							if (Math.floor(_width)>9){
								strees_level_map["level"] =  "low";
								var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
								strees_level_map["setcount"] = setcnt+1
							}
						}
						
					} else if (highway == "footway"){
						strees_level_map["level"] =  "low";
						var setcnt = strees_level_map["setcount"]  // strees_level_map.get("setcount")
						strees_level_map["setcount"] = setcnt+1
					}
				}
			}
			var strees_level = strees_level_map["level"] // strees_level_map.get("level");
			if (strees_level == "low"){
				layer0.apply(command.change(dataset.way(way.id), {
					tags: {
						stress_level: "low"
					}
				}));
				way.setModified(true);
			} else {
				layer0.apply(command.change(dataset.way(way.id), {
					tags: {
						stress_level: "high"
					}
				}));
				way.setModified(true);
			}
		}
			
}


function save_fun(){
	var layer2 = current_layer();
	var dataset2 = layer2.data;
	dataset2.save("/home/arminakvn/Google Drive/mapc_works/studyarea_stress.osm")
}
main_fun();
save_fun();
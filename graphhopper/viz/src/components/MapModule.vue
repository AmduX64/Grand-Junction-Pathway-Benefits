<template>
    <div id="map">
        <markers-module
            v-on:updateRoute="updateMyRoute()"
            v-bind:waypoints="waypoints"
    ></markers-module>
    </div>

</template>
<script>
    import * as mapboxgl from 'mapbox-gl'
    import MarkersModule from './MarkersModule'
    export default {
        name: 'MapModule',
        props:["features","anchors","waypoints"],
        components:{
            MarkersModule
        },
        // data:  function(){
        //     return {lineFeatures:[]}
        // },
        // watch:{
        //     features(){
        //
        //     }
        // },

        computed:{
            // average_speed: function(){
            //     return this.$props.features["details"]["average_speed"]
            // },
            // weight_value: function(){
            //     return this.$props.features["details"]["weight_value"]
            // },
            // street_name: function(){
            //     return this.$props.features["details"]["street_name"]
            // },
            // points: function(){
            //     return this.$props.features["points"]["coordinates"]
            // },
            // lineFeatures: function(){
            //     const self = this;
            //     let dateArr = []
            //     this.$props.features["details"]["weight_value"].forEach(function(event){
            //         // let d = event.path
            //         console.log("event",event);
            //         // get the points' coordinate for this segment
            //         let fromlon = self.$props.features["points"]["coordinates"][event[0]][0]
            //         let fromlat = self.$props.features["points"]["coordinates"][event[1]][1]
            //         console.log(fromlon,fromlat)
            //         let weight_val = event[2]
            //         console.log("weight val", weight_val)
            //         dateArr.push(
            //             {'type': 'Feature',
            //                 'properties': {
            //                     'color': '#F7455D',
            //                     'width': weight_val
            //                 },
            //                 'geometry':{
            //                     'type': 'LineString',
            //                     'coordinates': [[fromlon,fromlat]]
            //                 }
            //             }
            //         )
            //         console.log(dateArr)
            //         // dateArr
            //     })
            //     return dateArr
            // }
            // features: function(){
            //     // const self = this;
            //     return this.$parent.items;
            //     // console.log(this)
            //     // return self.
            //
            // }
        },
        methods:{
            updateMyRoute: function () {
                console.log("routechnage")
            }
        },
        mounted(){
            let self = this;
            // this.$nextTick(() => {
                mapboxgl.accessToken = 'pk.eyJ1IjoiYXJtaW5hdm4iLCJhIjoiSTFteE9EOCJ9.iDzgmNaITa0-q-H_jw1lJw'//'pk.eyJ1IjoiYXJtaW5hdm4iLCJhIjoiSTFteE9EOCJ9.iDzgmNaITa0-q-H_jw1lJw';
                self.map = new mapboxgl.Map({
                    container: self.$el,
                    style:'mapbox://styles/arminavn/cjolq7bui0wjm2spp1bfrozbi', // 'mapbox://styles/arminavn/cj8xnnjkycd4o2ss9c2rofh9p', //'mapbox://styles/arminavn/cjolq7bui0wjm2spp1bfrozbi', //'mapbox://styles/arminavn/cj8xnnjkycd4o2ss9c2rofh9p', 'mapbox://styles/arminavn/cjg2o1xyt08gn2rqocqpyq4k6'
                    center: [-71.068964, 42.347643],
                    zoom: 14
                });


                self.map.on('load', function() {
                    // afterMap.addSource("points", {
                    //     "type": "geojson",
                    //     "data": nyGeo
                    // })
                    let maplayer = {
                        'id': 'lines',
                        'type': 'line',
                        'type': 'line',
                        'source': {
                            'type': 'geojson',
                            'data': {
                                'type': 'FeatureCollection',
                                'features': self.$props.features
                            }
                        },
                        'paint': {
                            'line-width': ['get', 'width'],

                            // Use a get expression (https://www.mapbox.com/mapbox-gl-js/style-spec/#expressions-get)
                            // to set the line-color to a feature property value.
                            'line-color': ['get', 'color'],
                            'line-opacity':['get','opacity'],
                            // 'line-extrusion-height': ['get', 'height'],
                            // 'line-extrusion-base': ['get', 'base_height'],

                        }
                    }
                    let titlesLayer = {
                        'id': 'symbols',
                        'type': 'symbol',
                        'source': {
                            'type': 'geojson',
                            'data': {
                                'type': 'FeatureCollection',
                                'features': self.$props.features
                            }
                        },
                        "layout": {
                            "symbol-placement": "line",
                            "text-font": ["Open Sans Regular"],
                            "icon-allow-overlap":true,
                            "text-field": '{title}', // part 2 of this is how to do it
                            "text-size": 12,
                            "text-anchor":  "top"
                        }
                    }
                    let anchorsLayor = {
                        'id': 'symbolsancs',
                        'type': 'symbol',
                        'source': {
                            'type': 'geojson',
                            'data': {
                                'type': 'FeatureCollection',
                                'features': self.$props.anchors
                            }
                        },
                        "layout": {
                            "symbol-placement": "line-center",
                            "icon-allow-overlap":true,
                            "icon-padding":4,
                            "icon-offset":[10,10],
                            "text-font": ["Open Sans Regular"],
                            // "icon-image": "{icon}-15",
                            "text-field": '{total_weight}', // part 2 of this is how to do it
                            "text-size": 10,
                            "text-anchor":  "bottom-right"
                        }
                    }




                    self.map.addLayer(maplayer);
                    self.map.addLayer(titlesLayer);
                    self.map.addLayer(anchorsLayor);
                    // self.map.setPaintProperty('lines', 'line-opacity', .7);
                });

                // console.log("map layer", self.$props.features)

            // })
        },
        updated(){
            const self = this;
            // let dateArr = [];
            // console.log(self.lineFeatures)
            // const self = this;
            // let dateArr = []
            // console.log("lineFeatures calcs: ", self)
            //
            // self.$props.features["details"]["weight_value"].forEach(function(event){
            //     // let d = event.path
            //     console.log("event",event);
            //     // get the points' coordinate for this segment
            //     let fromlon = self.$props.features["points"]["coordinates"][event[0]][0]
            //     let fromlat = self.$props.features["points"]["coordinates"][event[1]][1]
            //     console.log(fromlon,fromlat)
            //     let weight_val = event[2]
            //     dateArr.push(
            //         {'type': 'Feature',
            //             'properties': {
            //                 'color': '#F7455D',
            //                 'width': weight_val
            //             },
            //             'geometry':{
            //                 'type': 'LineString',
            //                 'coordinates': [[fromlon,fromlat]]
            //             }
            //         }
            //     )
            //     console.log(dateArr)
            //      // dateArr
            // })


            // return dateArr
            // // console.log(mapboxgl)


            // self.updateMap()


            // const dateArr = [];


            // d3.queue().defer(d3.json,"localhost:8989/route?point=42.344161%2C-71.069104&point=42.337919%2C-71.09727&locale=en-US&vehicle=mapcrider2&weighting=fastest&elevation=true&use_miles=true&details=weight_value").await(function(err,d){
            //     self.items = d
            //     console.log("d : ",d)
            //
            // })




            //  let self = this;
            //  console.log("fff",self,document)
            //  document.addEventListener('scroll', self.handleScroll);
        }
    }
</script>
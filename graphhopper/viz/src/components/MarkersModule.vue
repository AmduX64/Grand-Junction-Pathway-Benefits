<template>

    <pre id='coordinates' class='coordinates'></pre>
</template>

<script>
    import * as mapboxgl from 'mapbox-gl'
    export default {
        name:"MarkersModule",
        props:["waypoints"],
        methods:{
            onDragEnd: function() {
                let self = this;
                let lngLat = self.marker.getLngLat();
                // coordinates.style.display = 'block';
                // coordinates.innerHTML = 'Longitude: ' + lngLat.lng + '<br />Latitude: ' + lngLat.lat;
                console.log(lngLat.lng.toString())
                let routeQ = "/point="+lngLat.lng.toString()+","+lngLat.lat.toString()+"&point="
                console.log(routeQ)
                self.$router.push(routeQ);
                // this.currentRoute = this.$route.params.route;
                // this.$emit('updateRoute',this.currentRoute)
            }
            //     getFeats: function () {
            //         return self.lineFeatures
            //     }
        },
        computed:{
            markerLatLng: function () {
             return this.$props.waypoints[0]["geometry"]["coordinates"][0]
            }
        },
        watch:{

            '$route.params.route':{
                handler(){
                    this.currentRoute = this.$route.params.route;
                    this.$emit('updateRoute',this.currentRoute)
                },
                immediate: true,
            }
        },
        mounted(){

           let self = this;
            //
            //
            // .on('load', function() {
            this.$nextTick(() => {
                self.$parent.map.on('load', function() {
                    // canvas = self.map.getCanvasContainer();
                    // console.log(self.$props.waypoints[0])
                    // console.log("self.$parrent.map", self.$parent.map)
                    self.marker = new mapboxgl.Marker({
                        draggable: true
                    })
                        .setLngLat(self.markerLatLng)
                        // .addTo(self.$parent.map);
                    self.marker.on('dragend', self.onDragEnd);
                })
            // })
        }
            )}

    }


</script>
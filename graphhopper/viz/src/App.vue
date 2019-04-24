
<template>
    <div id="app">
        <!-- <div class="background-image">
          </div> -->
        <div class="app-content">
            <div>bbfbfbfbjjjjd</div>
            <router-view/>
        </div>
    </div>

</template>
<script>
    import MapView from './components/MapView.vue'


    export default {
        name: 'app',
        components:{
            MapView
        },
        methods: {



            },



    }

</script>

<style >
    /*@import url('https://api.tiles.mapbox.com/mapbox-gl-js/v0.52.0/mapbox-gl.css');*/

    body{
        height: 100%;
    }
    #map { position:absolute; top:0; bottom:0; width:100%; }

</style>
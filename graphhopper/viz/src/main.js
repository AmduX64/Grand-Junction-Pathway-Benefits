import Vue from 'vue'
import App from './App.vue'
import router from './router'
// import mapboxgl from 'mapbox-gl'
import 'mapbox-gl/dist/mapbox-gl.css'
new Vue({
    router,
    render: h => h(App)
}).$mount('#app')

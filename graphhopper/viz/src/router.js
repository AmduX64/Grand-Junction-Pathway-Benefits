import Vue from 'vue'
import Router from 'vue-router'
import MapView from './components/MapView.vue'
Vue.use(Router)

export default new Router({
    routes:[
        { path: '/', redirect: '/point=42.355097%2C-71.061361&point=42.318246%2C-71.105052' },//
        {
            path: '/:route',
            name: 'mapview',
            component: MapView,
            props: true
        }
    ]
})
///point=42.355278%2C-71.0616&point=42.337207%2C-71.089543


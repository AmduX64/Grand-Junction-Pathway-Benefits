<template>
    <pre id='coordinates' class='coordinates'>

        <i>line widths are proportional to
        each segments' weight</i>

        <i>number of alternatives:</i> <strong>{{num_alternatives}}</strong>
        <i>time for the best route:</i> <strong>{{total_time}} minutes</strong>
        <i>total weight for the best route:</i> <strong>{{total_weight}}    </strong>

    </pre>
</template>

<script>
    export default {
        name: "InfoboxView",
        props:["feautre"],
        computed:{
            num_alternatives: function(){
                return this.$parent.dataitems.length;
            },
            total_weight:function(){
                return Math.round(this.$parent.dataitems[0]['weight']);
            },
            total_time: function () {
                // this.$parent.dataitems.forEach(function (ec) {
                //     // console.log
                //     let best_time = ec.time
                //     return best_time;
                // })

                return Math.round(this.$parent.dataitems[0]['time'] / 60000);
            }
        },
        mounted(){
            // coordinates.style.display = 'block';
            // coordinates.innerHTML ='<br />Latitude: ';
        }
    }
</script>

<style>
    .coordinates {
        background: rgba(0,0,0,0.5);
        color: #fff;
        position: absolute;
        bottom: 40px;
        left: 20px;
        padding:5px 5px 5px 5px;
        margin: 5px 5px 5px 5px;
        /*max-width: 300px;*/
        font-size: 12px;
        line-height: 18px;
        border-radius: 3px;
        display: block;
    }
</style>
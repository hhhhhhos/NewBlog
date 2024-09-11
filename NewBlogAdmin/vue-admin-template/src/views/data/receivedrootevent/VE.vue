<template>
  <div>

  </div>
</template>

<script>
// 总线事件接收组件

export default {
    props:{
        event_name:String // 
    },
    data() {
        return{
            const_event_name:""
        }
    },
    methods:{
        handleParentEvent(data){
            console.log("监听到总线事件"+this.const_event_name+",触发")
            this.$emit('rootEvent',data)
        }
    },
    created() {
    // 在组件创建时监听自定义事件
        this.$root.$on(this.event_name, this.handleParentEvent);
        this.const_event_name = this.event_name
    },
    mounted(){

    },
    beforeDestroy() {
    // 在组件销毁前移除事件监听器
        this.$root.$off(this.const_event_name, this.handleParentEvent);
    }
}
</script>

<style scoped>

</style>

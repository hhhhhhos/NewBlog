<template>
  <div>
    <pinglun pinglun_id="45699"/>
  </div>
</template>

<script>
import pinglun from '@/views/product/VE.vue'

export default {
    components:{
        pinglun
    },
    data() {
        return{
        }
    },
    methods:{
    },
    mounted() {
        this.$store.state.zhezhao_show = false
    }
}
</script>

<style scoped>

</style>

<template>
  <div>
    <el-empty :description=msg>
      <el-button type="primary" @click="returnhome">返回首页</el-button>
    </el-empty>
  </div>
</template>

<script>

export default {

    components: {
    },
    data() {
        return{
            msg:"404 not found"
        }
    },
    methods:{
        returnhome(){
            this.$router.push('/')
        },
        setmsg(){
            if(this.$route.query.msg) this.msg = this.$route.query.msg
        }
    
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.setmsg()
    }
}
</script>


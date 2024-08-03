<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <br><br><br>
    <p>微信登录正验证，请稍等……</p>
    <router-view/>

  </div>
</template>

<script>
import axios from '@/utils'

export default {
    components: {
    },
    data() {
        return{
            show:true
        }
    },
    methods:{
    },
    mounted(){
        this.$store.state.zhezhao_show = false  

        // 假设这里是你获取URL参数的代码
        const code = this.$route.query.code;
        const state = this.$route.query.state;
        console.log(state)

        axios.post('/user/loginByWechat',{
            code,
            state,
            is_mobile:state?state.startsWith('toutie')?false:this.$store.state.IsMobile:false
        }).then(response=>{
            if(response.data.code===0){
                this.$message.error(response.data.msg)
                if(response.data.msg==="已登录，不能重复登录"){
                    //服务器已登录 但本地记录没登录，就同步个名字,状态改成登录
                    axios.get('/user/name')
                        .then(response=>{
                            //console.log("名字是："+response.data.data)
                            this.$store.state.IsLogin = true
                            this.$store.state.UserName = response.data.data
                            setTimeout(() => {this.$router.push('/home')}, 1000);
                        })
                        .catch(error=>{
                            this.$message.error(error.data.msg);
                            console.log(error)
                        })
                }

            }
            else {
                this.$message.success(response.data.data)
                this.$store.state.IsLogin = true
                this.$store.state.UserName = this.name
                setTimeout(() => {this.$router.push('/home')}, 1000);
            }
            console.log(response)
        }).catch(error=>{
            this.$message.error(error.data.msg);
            console.log(error)
        })

      
    }
}
</script>

<style scoped>

</style>

<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <br><br><br>
    <p>微信登录正验证，请稍等……</p>
    <router-view/>

  </div>
</template>

<script>
import axios from '@/utils/axios';
import { setToken } from '@/utils/auth'

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

        axios.post('/user/loginByWechat',{
            code,
            state,
            is_mobile:state.startsWith('toutie')?false:this.$store.state.app.is_mobile
        }).then(response=>{
            if(response.data.code===0){
                this.$message.error(response.data.msg)
                if(response.data.msg==="已登录，不能重复登录"){
                    setToken(123)
                    setTimeout(() => {this.$router.push('/dashboard')}, 1000);
                }

            }
            else {
                this.$message.success(response.data.data)
                //this.$store.state.IsLogin = true
                //this.$store.state.UserName = this.name
                setToken(123)
                setTimeout(() => {this.$router.push('/dashboard')}, 1000);
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

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import axios from '@/utils/axios';
import { setToken } from '@/utils/auth'

export default {
    name: 'App',
    mounted(){
        axios.get('/user/session').then(response=>{
            var IsLogin = false
            try{
                response.data.forEach(item=>{
                    if(item.includes("IsLogin: "))
                        this.$store.state.app.UserId = item.replace("IsLogin: ",""),IsLogin=true
                })
                setToken(123)
            }catch(err){
                console.log(err)
            }
            
            if(!IsLogin){
                this.$router.push('/login')
            }
        })
    }
}
</script>

<style>
:root {
  --third-color:#2c3e50d0;
}

.bo_bu > .el-dropdown-menu__item{
  border-bottom: 1px solid gainsboro;
}
.el-dropdown-menu {
  padding: 0!important;
}

.fenlei .el-form-item__label{
    text-align: center !important;
}

.el-message{
    border-radius: 25px !important;
    border:1px solid transparent !important;
}
.el-message--success{
    background-color:var(--third-color) !important;
    border:1px solid var(--third-color) !important;
}
.el-message--error{
    background-color:var(--third-color) !important;
    border:1px solid var(--third-color) !important;
}
</style>

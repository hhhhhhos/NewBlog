<template>
    <div v-if="!this.$store.state.IsMobile" class="login-box">
      <h2>用户注册</h2>
      <form>
        <input type="text" v-model=name placeholder="用户名">
        <input type="password" v-model=password placeholder="密码">
        <input type="password" v-model=password2 placeholder="再输入一次">
        <p><a href="#" @click="$emit('ChangeToLogin')">去登录</a></p>
        <el-button type="submit" @click="regis">注册</el-button>
      </form>
    </div> 

    <div v-else class="login-box mobile" style="background-color: rgb(243, 243, 244);">
      <h2>用户注册</h2>
      <form>
        <input type="text" v-model=name placeholder="用户名">
        <input type="password" v-model=password placeholder="密码">
        <input type="password" v-model=password2 placeholder="再输入一次">
        <p><a href="#" @click="$emit('ChangeToLogin')">去登录</a></p>
        <el-button type="submit" @click="regis">注册</el-button>
      </form>
    </div> 

</template>

<script>
import axios from '@/utils'

export default {
  data() {
    return{
      name:null,
      password:null,
      password2:null
    }
  },
  methods:{
    regis(){
      if(this.name===null)return this.$message.error("用户名不能为空")
      if(this.password===null)return this.$message.error("密码不能为空")
      if(this.password2===null)return this.$message.error("请再输入一次")
      if(this.password!==this.password2)return this.$message.error("两次密码不一致")

    axios.post('/user/regis',{
        name:this.name,
        password:this.password
      }).then(response=>{
        if(response.data.code===0)this.$message.error(response.data.msg)
        else {
          this.$message.success(response.data.data)
          this.$emit('ChangeToLogin')
        }
        console.log(response)
      }).catch(error=>{
        this.$message.error(error.data.msg);
        console.log(error)
      })
    }
  },
  created(){
    console.log("login!!")
  }
}
</script>

<style scoped>
.login-box {
  width: 350px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  margin-top: 100px;
}

.mobile {
  box-shadow: 0 0 0;
  border-radius: 0;
}

.login-box h2 {
  text-align: center;
  font-size: 26px;
  margin-top: 0;
}

.login-box form {
  display: flex;
  flex-direction: column;
}

.login-box input {
  height: 35px;
  margin: 10px 0;
  padding: 10px;
  border-radius: 5px;
  border: none;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.login-box button {
  height: 45px;
  margin-top: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
}

.login-box button:hover {
  background-color: #0062cc
} 
</style>
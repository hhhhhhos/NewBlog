<template>
  <div>

    <div v-if=false>
      <div :style="'position: relative;height:'+ this.$store.state.CURRENT_HEIGHT*0.2 +'px;'">
        <div :style="'position: absolute;left:13%;top: '+this.$store.state.CURRENT_HEIGHT*0.07+'px;font-family: PingFang SC;font-weight:900 ;letter-spacing: 3px;font-size: '+this.$store.state.CURRENT_HEIGHT*0.04+'px;text-shadow: 5px 5px 4px rgba(0,0,0,0.5);'">
          <router-link to="/home">席巴商城</router-link>
        </div>
        <!-- 登录页 -->
        <div :style="'position: absolute;right: '+this.$store.state.CURRENT_HEIGHT*0.2+'px;top:'+this.$store.state.CURRENT_HEIGHT*0.2+'px;'">
          <div v-if="!IsRegis" class="login-box">
            <h2>邮箱登录</h2>
            <form style="justify-content: left;">
              <input type="text" v-model=name placeholder="邮箱账号">
              
              <div style="display: flex; align-items: center;">
                <input type="text" v-model="password" placeholder="验证码" style="margin-right: 8px;">
                <el-button :style="`padding:0 20px;margin: 0;height: 55px;width: 100%;text-align: center;${bg}`" :disabled="is_button_disable" @click="sendCode">{{count_title}}</el-button>
              </div>

              <el-button type="submit" @click="login">登录</el-button>
            </form>
          </div>
        </div>
      </div>
      <div class="fuck" :style="'height:'+ this.$store.state.CURRENT_HEIGHT*0.6 +'px;'"></div>
    </div>

    <div v-else style="display: flex;justify-content: center;align-items: center;">

      <div>
          <div class="login-box mobile" style="background-color: rgb(243, 243, 244);">
            <h2>邮箱登录</h2>
            <form>
              <input type="text" v-model=name placeholder="邮箱账号">
              <div style="display: flex; align-items: center;">
                <input type="text" v-model="password" placeholder="验证码" style="margin-right: 8px;">
                <el-button :style="`padding:0 20px;margin: 0;height: 55px;width: 100%;text-align: center;${bg}`" :disabled="is_button_disable" @click="sendCode">{{count_title}}</el-button>
              </div>
              <el-button type="submit" @click="login">登录</el-button>
            </form>
          </div> 

      </div>

      <div style="height: 200px;background-color: aqua;">
      </div>

    </div>


  </div>
</template>

<script>
import axios from '@/utils'


export default {
    components:{

    },
    data() {
        return{
            name:'',
            password:"",
            IsRegis:false,
            count_title:"发送验证码",
            is_button_disable:false, 
            count_save:null, // 暂存地址
            bg:""
        }
    },
    methods:{
    // 邮箱登录
        login(){
            if(!this.name.length)return this.$message.error("邮箱不能为空")
            if(!this.password)return this.$message.error("验证码不能为空")
            if(this.password.length!==6)return this.$message.error("验证码为6位")
            if(!this.name.length)return this.$message.error("邮箱不能为空")
            var re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
            if(!re.test(this.name))return this.$message.error("邮箱格式错误")

            axios.post('/user/loginByEmail',{
                email:this.name,
                code:this.password
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
        },
        login_wechat(){
            // 重定向页
            console.log(process.env.VUE_APP_REDIRECT_URI)
            // 微信要的state 每次都要不一样
            var state = this.generateUUID()
            window.location.href =`https://open.weixin.qq.com/connect/qrconnect?appid=wxa631fd7ca89e35a9&redirect_uri=${process.env.VUE_APP_REDIRECT_URI}&response_type=code&scope=snsapi_login&state=${state}#wechat_redirect`
        },
        generateUUID() {
            return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            );
        },
        // 请求给邮箱发验证码
        sendCode(){
            if(!this.name.length)return this.$message.error("邮箱不能为空")
            var re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/
            if(!re.test(this.name))return this.$message.error("邮箱格式错误")
            this.count_title = 30;
            this.is_button_disable = true
            this.bg = "background-color: darkgray;"

            // 发送网络请求
            this.sendCode_axios()
      
            // 倒数30秒
            this.count_save = setInterval(() => {
                if (this.count_title > 0) {
                    this.count_title--;
                } else {
                    clearInterval(this.count_save);
                    this.count_title = "发送验证码"
                    this.is_button_disable = false
                    this.bg = ""
                }
            }, 1000);
        },
        // axios请求 被sendCode内部调用
        sendCode_axios(){
            axios.get('/user/sendEmail',{params: {email:this.name}})
                .then(response=>{
                    if(response.data.code){
                        this.$message.success("邮件已发送")
          
                    }
                    else this.$message.error(response.data.msg)
                    console.log(response)
                }).catch(error=>{
                    this.$message.error(error.data.msg);
                    console.log(error)
                })
        }
    
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        // 初始化时 检查一次本地和
        //服务器已登录 但本地记录没登录，就同步个名字,状态改成登录
        axios.get('/user/name')
            .then(response=>{
                if(response.data.code){
                    this.$store.state.IsLogin = true
                    this.$store.state.UserName = response.data.data
                }else{
                    //this.$message.error("error:"+response.data.msg);
                    this.$store.state.IsLogin = false
                }
            })
            .catch(error=>{
                this.$message.error(error.data.msg);
                console.log(error)
                this.$store.state.IsLogin = false
            })

        // 已登录 跳转首页 不能再登录
        if(this.$store.state.IsLogin){
            this.$message.error("已登录，不能再登录")
            this.$router.push("/home")
        }
    },
    created(){
        console.log("login!!")
    },
    watch:{
    // 守望者，已登陆不能访问本页
        '$store.state.IsLogin':function(){
            if(this.$store.state.IsLogin)this.$router.push('/home')
        }
    }
}
</script>

<style scoped>
.login-box {
  width: 350px;

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

.fuck{
  /*background-image: url(../assets/B2.webp);*/
  background-size:contain;
  background-position: center;
  background-repeat: no-repeat;
  background-color: rgb(106, 181, 1);
}

a:-webkit-any-link {
    text-decoration: none;
    color: rgba(0, 0, 0, 0.666);
}

a:hover{
  color:black;
}

</style>


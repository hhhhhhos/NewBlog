<template>
  <div>

    <div v-if=false>
      <div :style="'position: relative;height:'+ this.$store.state.CURRENT_HEIGHT*0.2 +'px;'">
        <div :style="'position: absolute;left:13%;top: '+this.$store.state.CURRENT_HEIGHT*0.07+'px;font-family: PingFang SC;font-weight:900 ;letter-spacing: 3px;font-size: '+this.$store.state.CURRENT_HEIGHT*0.04+'px;text-shadow: 5px 5px 4px rgba(0,0,0,0.5);'">
          <router-link to="/home">席巴商城</router-link>
        </div>
        <!-- 登录页 -->
        <div :style="'position: absolute;right: '+this.$store.state.CURRENT_HEIGHT*0.2+'px;top:'+this.$store.state.CURRENT_HEIGHT*0.1+'px;'">
          <div v-show="!IsRegis" class="login-box">
            <h2>用户登录</h2>
            <form>
              <input type="text" v-model=name placeholder="用户名 / 邮箱">
              <input type="password" v-model=password placeholder="密码">
              <div style="display: flex; align-items: center;position: relative;">
                <input type="text" v-model="captch" placeholder="验证码" style="margin-right: 8px;">
                <div style="background-color: brown;">
                  <img id="captchaImage"  @click="getCaptch" height="100%" width="100%" style="cursor: pointer;object-fit: contain;" />
                </div>
                <div v-if="Is_captchaLoading" class="spinner"></div>
              </div>
              <div style="margin: 20px 0;position: relative;">
                <div style="display: inline;">
                  <a  @click="IsRegis=true">去注册</a>
                </div>
                <div style="display: inline;margin-left: 80px;">
                  <a  @click="$alert('请用微信或邮箱登录后修改')">忘记密码</a>
                </div>
                <div style="display: inline;margin-left: 30px;">
                  <a  @click="$router.push('/login3')">邮箱验证码登录</a>
                </div>
                <img loading="lazy" :src="require(`@/assets/wechat_login.png`)" 
                  style="width: 35px;height: 35px;object-fit: cover;cursor: pointer;position: absolute;top:-5px;left: 24%;"
                  @click="login_wechat()">
                
              </div>
              <el-button type="submit" @click="login">登录</el-button>
            </form>
          </div> 
          <div v-show="IsRegis">
            <RE @ChangeToLogin="IsRegis=false"></RE>
          </div>
        </div>
      </div>
      <div class="fuck" :style="'height:'+ this.$store.state.CURRENT_HEIGHT*0.6 +'px;'"></div>
    </div>

    <div v-else style="display: flex;justify-content: center;align-items: center;">

      <div>
          <div v-if="!IsRegis" class="login-box mobile" style="background-color: rgb(243, 243, 244);border-radius: 20px;">
            <h2>用户登录</h2>
            <form>
              <input type="text" v-model=name placeholder="用户名 / 邮箱">
              <input type="password" v-model=password placeholder="密码">
              <div style="display: flex; align-items: center;position: relative;">
                <input type="text" v-model="captch" placeholder="验证码" style="margin-right: 8px;">
                <img id="captchaImage"  @click="getCaptch" height="100%" width="100%" style="cursor: pointer;object-fit: contain;"/>
                <div v-if="Is_captchaLoading" class="spinner"></div>
              </div>
              <div style="margin: 20px 0;">
                <div style="display: inline;">
                  <a href="#" @click="$message('已暂停注册功能')">去注册</a>
                </div>
                <div style="display: inline;margin-left: 20px;">
                  <a @click="$alert('请用微信或邮箱登录后修改')">忘记密码</a>
                </div>
                <div style="display: inline;margin-left: 30px;">
                  <a  @click="$router.push('/login3')">邮箱验证码登录</a>
                </div>
              </div>
              <el-button type="submit" @click="login">登录</el-button>
            </form>
          </div> 
          <div v-else>
            <RE @ChangeToLogin="IsRegis=false,getCaptch()"></RE>
          </div>
          <div class="login-choices">
            <img v-if="!IsRegis" loading="lazy" :src="require(`@/assets/wechat_login.png`)" 
            style="width: 40px;height: 40px;object-fit: cover;cursor: pointer;margin-top: 0;"
            @click="login_wechat()">
          </div>
          
      </div>

      
      <div style="height: 200px;background-color: aqua;">
      </div>
      

    </div>


  </div>
</template>

<script>
import axios from '@/utils'
import RE from '@/components/ElRegis.vue'

export default {
    components:{
        RE,
    },
    data() {
        return{
            name:'visitor',
            password:"111111",
            IsRegis:false,
            captchaBase64Img:"", //图
            captch:"", // 验证码
            Is_captchaLoading:true
        }
    },
    methods:{
        login(){
            if(this.name.length===0)return this.$message.error("用户名不能为空")
            if(this.password.length===0)return this.$message.error("密码不能为空")
            if(this.captch.length===0)return this.$message.error("验证码不能为空")
            if(this.captch.length!==4)return this.$message.error("验证码为4位数")
            axios.post('/user/login',{
                user:{
                    name:this.name,
                    password:this.password
                },
                captch:this.captch
            }).then(response=>{
                if(response.data.code===0){
                    this.$message.error(response.data.msg)
                    this.getCaptch()
                    if(response.data.msg==="已登录，不能重复登录"){
                        //服务器已登录 但本地记录没登录，就同步个名字,状态改成登录
                        axios.get('/user/name')
                            .then(response=>{
                                //console.log("名字是："+response.data.data)
                                this.$store.state.IsLogin = true
                                this.$store.state.UserId = response.data.map.user_id
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
                    this.$store.state.UserId = response.data.map.user_id
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

            // 获取 User Agent
            var is_in_wechat = false
            var userAgent = navigator.userAgent.toLowerCase();
            // 判断是否在微信中打开
            if (userAgent.indexOf('micromessenger') !== -1) {
                console.log('当前页面在微信中打开');
                is_in_wechat = true
            } else {
                console.log('当前页面不在微信中打开');
                is_in_wechat = false
            }

            // 电脑跳转 // 网页授权appid
            if(!this.$store.state.IsMobile)window.location.href =`https://open.weixin.qq.com/connect/qrconnect?appid=wxa631fd7ca89e35a9&redirect_uri=${process.env.VUE_APP_REDIRECT_URI}&response_type=code&scope=snsapi_login&state=${state}#wechat_redirect`
            // 手机浏览器跳转
            else if(!is_in_wechat) this.$router.push('url_scan')
            // 微信内页跳转 // 服务号授权appid
            else if(is_in_wechat) window.location.href =`https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxbc1cabc1fa496099&redirect_uri=${process.env.VUE_APP_REDIRECT_URI}&response_type=code&scope=snsapi_userinfo&state=${state}#wechat_redirect`
            else this.$message.error("参数错误")


        },
        generateUUID() {
            return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            );
        },
        getCaptch(){
            axios.get('/user/getCaptch', {
                responseType: 'blob'  // 告诉axios返回类型是blob
            })
                .then(response => {
                    const url = window.URL.createObjectURL(new Blob([response.data]));
                    // 假设你有一个img标签用来显示验证码
                    const img = document.getElementById('captchaImage');
                    if (img) {
                        img.src = url;
                        this.Is_captchaLoading = false
                    }
                })
                .catch(error => {
                    this.$message.error('Error fetching the captcha');
                    console.error(error);
                });
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
                    // region 跳转首路径
                    const redirect = sessionStorage.getItem('redirectPath') || '/';
                    sessionStorage.removeItem('redirectPath'); // 清除保存的路径
                    console.log("跳转："+redirect)
                    return this.$router.push(redirect); 
                    // endregion
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
        // 拿验证码
        this.getCaptch()

        // 已登录 跳转首页 不能再登录
        //if(this.$store.state.IsLogin){
        //  this.$message.error("已登录，不能再登录")
        //  this.$router.push("/home")
        //}

        // 跳转到微信内页为了微信授权的话 wechatnow === "true"
        if(this.$route.query.wechatnow === "true")this.login_wechat()
    },
    created(){
        console.log("login!!")
    },
    watch:{
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

a{
    text-decoration: none;
    color: rgba(0, 0, 0, 0.666);
    cursor: pointer;
}

a:hover{
  color:black;
}

.spinner {
    border: 4px solid rgba(0, 0, 0, 0.1);
    border-top: 4px solid #3498db;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    position: absolute;
    top: 20%;
    right: 5%;
    transform: translateX(-50%);
    animation: spin 2s linear infinite;
}

@keyframes spin {
    0% { transform: translateX(-50%) rotate(0deg); }
    100% { transform: translateX(-50%) rotate(360deg); }
    
}

.login-choices{
    margin-top: 4px;
    border-radius: 100px;
    background-color: var(--forth-color);
}


</style>


<template>
  <div class="login-container">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">

      <div class="title-container">
        <h3 class="title">西巴博客管理系统</h3>
      </div>

      <el-form-item prop="username">
        <span class="svg-container">
          <svg-icon icon-class="user" />
        </span>
        <el-input
          ref="username"
          v-model="loginForm.username"
          placeholder="Username"
          name="username"
          type="text"
          tabindex="1"
          auto-complete="on"
        />
      </el-form-item>

      <el-form-item prop="password">
        <span class="svg-container">
          <svg-icon icon-class="password" />
        </span>
        <el-input
          :key="passwordType"
          ref="password"
          v-model="loginForm.password"
          :type="passwordType"
          placeholder="Password"
          name="password"
          tabindex="2"
          auto-complete="on"
          @keyup.enter.native="handleLogin"
        />
        <span class="show-pwd" @click="showPwd">
          <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
        </span>
      </el-form-item>

      
      <div style="display: flex;position: relative;">
        
        <el-form-item style="flex-grow: 1;margin-right: 15px;" prop="captch"> 
          <span class="svg-container">
            <svg-icon icon-class="tree" />
          </span>
          <el-input v-model="loginForm.captch" tabindex="2" placeholder="验证码"/>
        </el-form-item>

        <div style="margin-bottom: 20px;min-width: 80px;max-width: 20vw;border-color: none;">
          <img id="captchaImage"  @click="getCaptch" height="90%" width="90%" style="cursor: pointer;object-fit: contain;" />
          <div v-if="Is_captchaLoading" class="spinner"></div>
        </div>
        
      </div>
       

      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:15px;" @click.native.prevent="handleLogin">登录</el-button>

      <div style="display: flex;justify-content: center;align-items: center;margin-top: 0;padding-top: 0;">
        <img class="weixin" v-if="!IsRegis" loading="lazy" :src="require(`@/assets/wechat_login.png`)" 
            style="width: 40px;height: 40px;object-fit: cover;cursor: pointer;margin-top: 0;border-radius: 20px;"
        @click="login_wechat()">
      </div>


    </el-form>
  </div>
</template>

<script>
import axios from '@/utils/axios';
import { setToken } from '@/utils/auth'

export default {
    name: 'Login',
    data() {
        return {
            loginForm: {
                username: 'visitor',
                password: '111111',
                captch:'',
            },
            loginRules: {
                username: [{ required: true, trigger: 'blur' }],
                password: [{ required: true, trigger: 'blur' }],
                captch: [{ required: true, trigger: 'blur' }],
            },
            loading: false,
            passwordType: 'password',
            redirect: '/',
            Is_captchaLoading:true
        }
    },
    watch: {
        $route: {
            handler: function(route) {
                this.redirect = route.query && route.query.redirect
            },
            immediate: true
        }
    },
    methods: {
        login_wechat(){
            // 重定向页
            console.log(process.env.VUE_APP_REDIRECT_URI)
            // 微信要的state 每次都要不一样
            var state = 'admin'+this.generateUUID()

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
            if(!this.$store.state.app.is_mobile)window.location.href =`https://open.weixin.qq.com/connect/qrconnect?appid=wxa631fd7ca89e35a9&redirect_uri=${process.env.VUE_APP_REDIRECT_URI}&response_type=code&scope=snsapi_login&state=${state}#wechat_redirect`
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
        showPwd() {
            if (this.passwordType === 'password') {
                this.passwordType = ''
            } else {
                this.passwordType = 'password'
            }
            this.$nextTick(() => {
                this.$refs.password.focus()
            })
        },
        handleLogin() {
            this.$refs.loginForm.validate(valid => {
                if (valid) {
                    this.loading = true
                    this.$store.dispatch('user/login', this.loginForm).then(() => {
                        this.$router.push({ path: this.redirect || '/' })
                        this.loading = false
                    }).catch(() => {
                        this.loading = false
                        this.getCaptch()
                    })
                } else {
                    console.log('error submit!!')
                    return false
                }
            })

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
        },
        getsession(){
            axios.get('/user/session').then(response=>{
                console.log(response.data)
                console.log(response.data[0])
                if(response.data[0]==="Role: admin"){
                    setToken(123)
                    this.$router.push({ path: this.redirect || '/' })
                }
                else{
                    console.log("not admin")
                    this.$message.error('只允许visitor或admin角色登录')
                }
            })
        }
    },
    mounted(){
        this.getCaptch()
        this.getsession()
    }
}
</script>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg:#283443;
$light_gray:#fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
  .login-container .el-input input {
    color: $cursor;
  }
}

.weixin:hover{
  background-color: #ffffffb7;
  transition: background-color 0.3s ease;
}

.weixin{
  transition: background-color 0.3s ease;
}

/* reset element-ui css */
.login-container {
  .el-input {
    display: inline-block;
    height: 47px;
    width: 85%;

    input {
      background: transparent;
      border: 0px;
      -webkit-appearance: none;
      border-radius: 0px;
      padding: 12px 5px 12px 15px;
      color: $light_gray;
      height: 47px;
      caret-color: $cursor;

      &:-webkit-autofill {
        box-shadow: 0 0 0px 1000px $bg inset !important;
        -webkit-text-fill-color: $cursor !important;
      }

    }
  }

  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.1);
    border-radius: 5px;
    color: #454545;
  }
}

.spinner {
    border: 4px solid rgba(0, 0, 0, 0.1);
    border-top: 4px solid #3498db;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    position: absolute;
    top: 10%;
    right: 1%;
    transform: translateX(-50%);
    animation: spin 2s linear infinite;
  }

  @keyframes spin {
    0% { transform: translateX(-50%) rotate(0deg); }
    100% { transform: translateX(-50%) rotate(360deg); }
    
  }
</style>

<style lang="scss" scoped>
$bg:#2d3a4b;
$dark_gray:#889aa4;
$light_gray:#eee;

.login-container {
  min-height: 100%;
  width: 100%;
  background-color: $bg;
  overflow: hidden;

  .login-form {
    position: relative;
    width: 520px;
    max-width: 100%;
    padding: 160px 35px 0;
    margin: 0 auto;
    overflow: hidden;
  }

  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;

    span {
      &:first-of-type {
        margin-right: 16px;
      }
    }
  }

  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
  }

  .title-container {
    position: relative;

    .title {
      font-size: 26px;
      color: $light_gray;
      margin: 0px auto 40px auto;
      text-align: center;
      font-weight: bold;
    }
  }

  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
}
</style>

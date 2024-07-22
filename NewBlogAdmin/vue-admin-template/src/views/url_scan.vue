<template>
  <!-- 这一页是为了手机浏览器的微信授权（不能点击后直接跳转微信打开网页，只能跳小程序） -->
  <div style="width: 100vw;height: 100vh;display: flex;align-items: center;justify-content: center;">
    
    <div v-if="ENV === 'development'">
      
      <div style="margin-top: 20px;">请在微信扫描二维码打开网页 <br>或直接在微信打开<br>http://www.yjzblog.site:48080/login?wechatnow=true</div>
      <el-button style="margin-top: 20px;" @click="toutie()">我头铁，我要直接扫码</el-button>
    </div>

    <div v-else>
      
      <div style="margin-top: 20px;">请在微信扫描二维码打开网页 <br>或直接在微信打开<br>https://www.yjztest.top/xiba-newblog/login?wechatnow=true</div>
      <el-button style="margin-top: 20px;" @click="toutie()">我头铁，我要直接扫码</el-button>
    </div>

    

  </div>
</template>

<script>
export default {
    data() {
        return {
            ENV:process.env.VUE_APP_ENV
        };
    },
    methods: {
        toutie(){
            // 电脑跳转 // 网页授权appid
            var state = 'toutie'+this.generateUUID()
            window.location.href =`https://open.weixin.qq.com/connect/qrconnect?appid=wxa631fd7ca89e35a9&redirect_uri=${process.env.VUE_APP_REDIRECT_URI}&response_type=code&scope=snsapi_login&state=${state}#wechat_redirect`
        },
        generateUUID() {
            return ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
                (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
            );
        },
    
    },
    mounted(){

        console.log(this.ENV)
    }
};
</script>

<style scoped>

</style>





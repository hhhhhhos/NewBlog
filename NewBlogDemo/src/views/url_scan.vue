<template>
  <!-- 这一页是为了手机浏览器的微信授权（不能点击后直接跳转微信打开网页，只能跳小程序） -->
  <div style="width: 100vw;height: 100vh;display: flex;align-items: center;justify-content: center;">
    
    <div v-if="ENV === 'development'">
      <img :src="require(`@/assets/dev_url.png`)" style="width: 70vw;">
      <div style="margin-top: 20px;">请在微信扫描二维码打开网页 <br>或直接在微信打开<br>http://www.yjzblog.site:48080/login?wechatnow=true</div>
    </div>

    <div v-else>
      <img :src="require(`@/assets/prod_url.png`)" style="width: 70vw;">
      <div style="margin-top: 20px;">请在微信扫描二维码打开网页 <br>或直接在微信打开<br>https://www.yjztest.top/xiba-shop/login?wechatnow=true</div>
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
    
  },
  mounted(){
    this.$store.state.zhezhao_show = false
    console.log(this.ENV)
  }
};
</script>

<style scoped>

</style>





<template>
  <div>
    <h1>后台界面</h1>
    <p>正在跳转...</p>
  </div>
</template>

<script>

export default {
  data() {
    return{
      admin_url:process.env.VUE_APP_AMDIN_URL
    }
  },
  methods:{
  },
  created(){
    window.location.href = `${process.env.VUE_APP_ADMIN_URL}`
  }
}
</script>

<style scoped>

</style>

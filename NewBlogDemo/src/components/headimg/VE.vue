<template>
    <img
        :style="the_style"
        :src="obj?.wechat_headimgurl?obj.wechat_headimgurl:obj?.email?.includes('@qq.com')?`https://q1.qlogo.cn/g?b=qq&nk=${obj.email.replace('@qq.com','')}&s=100`:target_img_src"
        @click="$router.push('/user/info/details')"
      >
</template>

<script>

export default {
    props:{
        obj:Object,
        target_img_src:String,
        the_style:String
    },
    data() {
        return{
        }
    },
    methods:{
    }
}
</script>

<style scoped>

</style>

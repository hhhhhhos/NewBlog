<template>
    <img
    title="查看个人信息"
        ref="myImage"
        :style="the_style"
        :src="obj?.wechat_headimgurl?
        obj.wechat_headimgurl:obj?.email?.includes('@qq.com')?
        `https://q1.qlogo.cn/g?b=qq&nk=${obj.email.replace('@qq.com','')}&s=100`:target_img_src"
        @click="click"
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
        click(){
            if(this.goanother())return
            if(this.obj?.user_id){
                this.$store.state.UserId===this.obj?.user_id?
                    this.$router.push('/user/info/details'):
                    this.$router.push('/user/otheruserinfo?user_id='+this.obj.user_id)
            }else{
                this.$store.state.UserId===this.obj?.id?
                    this.$router.push('/user/info/details'):
                    this.$router.push('/user/otheruserinfo?user_id='+this.obj.id)
            }
        },
        goanother(){
            if(this.$route.path==='/user/info/details'){
                if(this.obj?.id)this.$router.push('/user/otheruserinfo?user_id='+this.obj.id)
                else this.$router.push('/user/otheruserinfo?user_id='+this.obj.user_id)
                return true
            }else{
                if(this.$route.path==='/user/otheruserinfo'){
                    this.$openURL2(this.$refs.myImage.src)
                }
            }
            return false
        }

    }
}
</script>

<style scoped>

</style>

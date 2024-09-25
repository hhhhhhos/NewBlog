<template>
  <div class="my-container" v-loading="isloading1">
    
    <div style="display: flex;background-color: rgb(54, 170, 237);">
      
      <headimg 
      the_style="margin: 20px 0 25px 25px;cursor: pointer;width:70px;height: 70px;border-radius: 90px;border: 2px solid white;"  
      :target_img_src="target_img_src" 
      :obj="obj" />
      
      <div style="margin:35px 0 0 15px ;display: block;justify-content: left;">
        
        <div style="display: flex;">  
          
          <div @click="$router.push('/user/info/details')"  style="font-size: 18px;color: white;cursor: pointer;">
            {{obj?.wechat_nickname?obj.wechat_nickname:obj?.name?obj.name:"未知用户"}}
          </div>
          <img style="margin: 3px 0 0px 5px;" v-if="obj?.wechat_nickname?true:false" width="18px" height="18px" :src="require('@/assets/wechat_confirm.png')">
          <span style="margin: 5px 0 0 7px;color:white;font-size: 11px;display: flex;align-items: center;">
            <i class="el-icon-location-outline"></i>
              {{this?.obj?.ip_location?this.obj.ip_location:"公共厕所"}}
          </span>
        </div>

        <div style="color: white;font-size: 11px;margin: 10px 0 0 0;text-align: left;">
            ID:{{this.obj?.id?this.obj.id:"未知"}}  
        </div>

      </div>

    </div>

    <div class="grid-nav">
      <el-row style="background-color: white;width: 90%;z-index: 1;border-top-right-radius: 15px ;border-top-left-radius: 15px ;padding-bottom: 10px;">
        <el-col :span="8">
          <p @click="$router.push(`/user/info/dongtai`)"
          v-html="dt===-1?'<i class=\'el-icon-loading\'></i>':dt"></p>
          <span>动态</span>
        </el-col>
        <el-col :span="8">
          <p @click="$router.push(`/user/fanorguanzhu/fs/${obj.id}`)"
          v-html="fs===-1?'<i class=\'el-icon-loading\'></i>':fs"></p>
          <span>粉丝</span>
        </el-col>
        <el-col :span="8">
          <p @click="$router.push(`/user/fanorguanzhu/gz/${obj.id}`)"
          v-html="gz===-1?'<i class=\'el-icon-loading\'></i>':gz"></p>
          <span>关注</span>
        </el-col>
      </el-row>
    </div>
    
    <div style="text-align: left;margin: 0px 0 0 0;border-top: 1px solid beige;">
        <van-cell icon="chat-o" title-style="margin-left: 5px;" title=" 站内消息" is-link to='/user/chat/history' >
        <template #icon>
          <van-icon name="chat-o" :badge=$store.state.chat_badge size="16" style="margin-right: 4px;"/>
        </template>
      </van-cell>
      <van-cell icon="user-circle-o" title-style="margin-left: 5px;" title="详细信息" is-link to='/user/info/details' />
      <van-cell icon="edit" title-style="margin-left: 5px;" style="margin-top: 8px;" title="用户反馈" is-link @click.native="handleClick('feedback')" />
      <van-cell icon="smile-o" title-style="margin-left: 5px;" title="客服二号" is-link to="/kefu" />
      <van-cell icon="orders-o" title-style="margin-left: 5px;" title="客服记录" is-link to="/kefu/history" />
      <van-cell icon="setting-o" title-style="margin-left: 5px;" style="margin-bottom: 8px;" title="个人设置" is-link :to="`/user/info/option?id=${obj.id}`" />
      <van-cell style="cursor: pointer;text-align: center;height: 50px;align-items: center;font-size: medium;" title="退出登录"  @click="logout"  />
    </div>


  </div>
</template>

<script>
import { Cell } from 'vant';
import { Dialog } from 'vant';
import axios from '@/utils'
import { Icon } from 'vant';
import headimg from '@/components/headimg/VE.vue'

export default {
    name: 'MyIndex',
    components: {
        headimg,
        'van-cell':Cell,
        'van-icon':Icon,

    },
    props: {},
    data () {
        return {
            chat_badge:"",
            dt:-1,
            fs:-1,
            gz:-1,
            obj:null,
            target_img_src:require('@/assets/load.webp'),
            isloading1:true
        }
    },
    methods: {
        async test(){
            await this.getuserinfo()
            await this.getfaninfo()
      
        },
        handleClick(routeName) {
            console.log(routeName)
            Dialog.alert({message: "<h3>comming soon</h3>"})
        },
        async getuserinfo(){

            await axios.get('/user/info')
                .then(response=>{
                    if(response.data.code)this.isloading1=false
                    else this.$message.error("获取失败："+response.data.msg)
                    this.obj = response.data.data
                    if(this.obj?.wechat_headimgurl === null){
                        this.target_img_src = require('@/assets/default_headimg2.png')
                    }
                    const name = this.$store.state.UserName
                    if(this.obj?.name !== name && this.obj?.wechat_nickname !== name){
                        if(this.obj?.wechat_nickname)this.$store.state.UserName = this.obj?.wechat_nickname
                        else this.$store.state.UserName = this.obj?.name
                    }
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
        logout(){
            axios.get('/user/logout') // 似乎是唯一退出口
                .then(response=>{
                    console.log(response)
                    if(response.data.code){
                        this.$message.success("退出登录成功")
                        this.$store.state.IsLogin = false
                        this.$store.state.UserId = null
                        this.$store.state.UserName = null
                        this.$root.$emit('logout')
                        this.$router.push('/home')
                    }
                    else this.$message.error("退出失败："+response.data.msg)

                }).catch(error=>{
                    console.log(error)
                    this.$message.error("错误："+error.data.msg)
                })
        },
        async getfaninfo(){
            await axios.get(`/fan/info?user_id=${this.obj.id}`)
                .then(response=>{
                    if(response.data.code){
                        // 如果response.data.data未true表示已关注
                        this.gz = response.data.map.gz
                        this.fs = response.data.map.fs
                        this.dt = response.data.map.dt
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })

        },
        async getUnreadNum(){
            await axios.get('/chat/getUnreadNum')
                .then(response=>{
                    if(response.data.data!=='0')this.$store.state.chat_badge = response.data.data
                    else this.$store.state.chat_badge = ""
                })
        },
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.test()
        this.already_enter_once = true
    },
    // 已渲染，再次进入时不会触发mounted钩子 所有
    // eslint-disable-next-line no-unused-vars
    updated(){
        console.log("钩子测试")
        try{
            this.getUnreadNum()
        }catch(e){
            console.log('info异常:'+e)
        }
    },  
}
</script>

<style scoped>

.el-col > span{
  margin: 0;
  font-size: smaller;
}
.el-col > p{
  margin: 15px 0 5px 0;
  font-weight: bold;
}

.grid-nav{
  background-color: rgb(54, 170, 237);
  display: flex;
  justify-content: center;
}
.el-col > p{
  cursor: pointer;
}

</style>

<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div>
    <el-menu v-if="!this.$store.state.IsMobile" router style="background-color: white;"  class="el-menu-demo" mode="horizontal" >
      <el-menu-item index="/" style="font-size:15px;">欢迎来到阿西吧商城</el-menu-item>
      <el-menu-item v-if="!this.$store.state.IsLogin" index="/user/login">登录</el-menu-item>
      <el-menu-item v-else index="/user/info">欢迎，{{ this.$store.state.UserName }}</el-menu-item>
      <el-menu-item><button style="cursor: pointer;border: none;background-color: transparent;" @click="gotoback()">后台</button></el-menu-item>
      <el-menu-item index="/user/info" style="float:right;">个人信息</el-menu-item>
      <el-menu-item index="/user/buylist" style="float:right;" >购物车</el-menu-item>
      <el-menu-item index="/user/historylist" style="float:right;">历史订单</el-menu-item>
    </el-menu>

    <div v-else>
      <!-- 返回头标 -->
      <div v-if="true" class="demo-nav" style="">
        <div class="demo-nav__title">{{this.$route.meta.title.replace("席巴商城 -","")}}
          <span v-if="this.$route.name === 'kefu'"> {{ "- "+$store.state.kefu_name }}</span>
          <span v-if="this.$route.name === 'fanorguanzhu'"> {{ "- "+$store.state.fanorguanzhu_name }}</span>
          <span v-if="this.$route.name === 'userchat'" @click="$router.push(`/user/otheruserinfo?user_id=${$store.state.userchat_id}`)"> {{ "- "+$store.state.userchat_name }}</span>
        </div>
        <svg viewBox="0 0 1000 1000" class="demo-nav__back" @click="$router.go(-1)">
          <path fill="#969799" fill-rule="evenodd" d="M296.114 508.035c-3.22-13.597.473-28.499 11.079-39.105l333.912-333.912c16.271-16.272 42.653-16.272 58.925 0s16.272 42.654 0 58.926L395.504 498.47l304.574 304.574c16.272 16.272 16.272 42.654 0 58.926s-42.654 16.272-58.926 0L307.241 528.058a41.472 41.472 0 0 1-11.127-20.023z">
          </path>
        </svg>
        <!-- 右上角菜单图标 -->
        <van-icon @click="popup_show=true"  style="cursor: pointer;position: absolute;right: 0;margin-right: 10px;font-weight: bold;" name="wap-nav" />
      </div>
      <div v-if="this.$route.name !== 'home'" style="height: 56px;"></div>


      <Tabbar v-if="this.$store.state.PAGE_STATE==='Tabbar'" style="position:fixed;" route>
        <TabbarItem icon="home-o"  to="/home">主页</TabbarItem>
        <TabbarItem icon="completed-o" to="/user/historylist">订单</TabbarItem>
        <TabbarItem icon="cart-o" to="/user/buylist">购物车</TabbarItem>
        <TabbarItem icon="contact-o" to="/user/info" >个人信息</TabbarItem>
      </Tabbar>

      <!-- 右上角菜单弹出-->
      <van-popup v-model="popup_show" position="top" style="text-align: center;" >
        <div >
          <van-cell-group class="mycell">
            <van-cell  title="首页"  is-link to="/home" @click="popup_show=false"/>
            <van-cell  title="后台" is-link  :url=url @click="popup_show=false"/>
            <van-cell  title="历史聊天" is-link to="/user/chat/history" @click="popup_show=false">
              <template #title>
                <span class="custom-title" style="position: relative;">
                  历史聊天
                  <div v-if="$store.state.chat_badge!==''"  style="margin: 10px 0px 0 0;margin-left: auto;text-align: center;
                  border-radius: 20px;background-color: red;color: white;width: 20px;font-size: 12px;line-height: 18px;
                  position: absolute;bottom: -1px;right:-25px;">
                    {{$store.state.chat_badge}}
                  </div>
                </span>
              </template>
            </van-cell>
            <van-cell  title="个人信息" is-link  to="/user/info" @click="popup_show=false" />
          </van-cell-group>
        </div>  
      </van-popup>

    </div>

    <router-view/>

    <div style="height: 100px;width: 20px;"></div>

    <!--加载遮罩-->
    <div v-if="$store.state.zhezhao_show" class="zhezhao">
      <i class="el-icon-loading"></i>
    </div>

    <!--websocket推送实时新消息-->
    <websocket />

    <!--接收websocket的总线事件-->
    <rootEvent event_name="socket_received_msg" @rootEvent="reLoadOne" />

    <!--websocket推送上栏  推送不在 user/chat 和 user/chat/history时 推两秒-->
    <div class="card_out_top" :style="card_out_margin_top" 
    @touchstart="handleTouchStart"
    @touchend="handleTouchEnd"
    @click="$router.push(`/user/chat?receiver_id=${column?.last_sender_id}`)">
      <div class="card_in_top">
        
        <img style="margin: auto 0;cursor: pointer;width:50px;height: 50px;border-radius: 90px;border: 2px solid white;"
        :src="column?.receiver.wechat_headimgurl?column.receiver.wechat_headimgurl:require('@/assets/default_headimg5.webp')"
        >
        <div>
          <!-- 名 -->
          <div style="text-align: start;margin: 5px 0 8px 10px;font-weight: bolder;font-size: small;">
            {{column?.receiver.wechat_nickname?column.receiver.wechat_nickname:column?.receiver.name}}
          </div>
          <!-- 最新一条内容 -->
          <div style="margin: 10px 0 0 10px;color: rgb(100,100,100,0.6);text-align: start;font-size: small;">
            {{column?.last_info}}
          </div>
        </div>

      </div>
    </div>

    


  </div>
</template>


<script>
import axios from '@/utils'
import { Tabbar, TabbarItem } from 'vant';
import { Icon } from 'vant';
import { Popup } from 'vant';
import { Cell, CellGroup } from 'vant';
import websocket from '@/components/websocket/VE.vue'
import rootEvent from '@/components/receivedrootevent/VE.vue'

export default {
  components:{
    Tabbar,
    TabbarItem,
    'van-icon':Icon,
    'van-popup':Popup,
    'van-cell':Cell,
    'van-cell-group':CellGroup,
    websocket,
    rootEvent
  },
  data() {
    return{
      column:null,
      card_out_margin_top:'margin-top:-100px;',
      url:process.env.VUE_APP_ADMIN_URL,
      popup_show:false
    }
  },
  methods:{
    async getUnreadNum(){
      await axios.get('/chat/getUnreadNum')
      .then(response=>{
          if(response.data.data!=='0')this.$store.state.chat_badge = response.data.data
          else this.$store.state.chat_badge = ""
      })
    },
    reLoadOne(data) {
      // 获取未读聊天数
      this.getUnreadNum()
      // /chat/history和 当事人聊天 不弹窗
      if (this.$route.name === 'userchathistory' || 
        (this.$store.state.userchat_id === data.chatHistoryItemVo.last_sender_id && this.$route.name === 'userchat')) return;
      console.log(data);
      this.column = data.chatHistoryItemVo;
      this.card_out_margin_top = 'margin-top:0px;';
      
      // Clear previous timeout if any
      if (this.hideTimeout) {
        clearTimeout(this.hideTimeout);
      }
      
      // Set timeout to hide the card
      this.hideTimeout = setTimeout(() => {
        this.card_out_margin_top = 'margin-top:-100px;';
      }, 2000);
    },
    handleTouchStart() {
      if (this.hideTimeout) {
        clearTimeout(this.hideTimeout);
        this.hideTimeout = null;
      }
    },
    handleTouchEnd() {
      // Slightly longer timeout to avoid immediate hiding on touch end
      this.hideTimeout = setTimeout(() => {
        this.card_out_margin_top = 'margin-top:-100px;';
      }, 3000);
    },
    gotoback(){
      window.open(process.env.VUE_APP_ADMIN_URL, '_blank');
    },
    handleSelect(){
    },
    tohome(){
      if(this.$route.path!=='/')this.$router.push('/')
      else location.reload()
    }
  },
  mounted(){
    this.$store.state.zhezhao_show = false
    this.getUnreadNum()
    // 初始化时 检查一次本地和
    //服务器已登录 但本地记录没登录，就同步个名字,状态改成登录
    axios.get('/user/name')
    .then(response=>{
      if(response.data.code){
        this.$message.success({
          showClose: true,
          message:"欢迎回来："+response.data.data
        });
        this.$store.state.IsLogin = true
        this.$store.state.UserName = response.data.data
        this.$store.state.UserId = response.data.map.user_id
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
  }
}
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;

}
.el-menu--horizontal > .el-menu-item {
  border-bottom: none !important;
  background-color: transparent !important;
  color: inherit !important;
}
.el-menu-item:hover {
  border-bottom: none !important;
  background-color:white !important;
}
.homehover:hover{
  background-color: red;
}

.card_out_top{
  position: fixed;
  width:100%;
  display: flex;
  justify-content: center;
  align-items: center;
  top:0;
  z-index: 9999;
  transition: margin-top 0.7s ease;
}

.card_in_top{
  background-color: rgba(242, 242, 242, 0.894);
  margin: 8px 0 0 0;
  padding: 10px 10px;
  display:flex;
  cursor: pointer;
  width:85%;
  border-radius: 10px;
}
</style>

<style>
.demo-nav {
    position: fixed;  /* 从 relative 改为 fixed */
    top: 0;           /* 定位到页面顶部 */
    left: 0;          /* 定位到页面左边 */
    width: 100%;      /* 让导航栏宽度扩展至全屏 */
    display: flex;
    align-items: center;
    justify-content: center;
    height: 56px;
    background-color: #fff;
    z-index: 1000;    /* 确保导航栏位于其他内容之上 */
}

.demo-nav__title {
    font-weight: 600;
    font-size: 17px;
    text-transform: capitalize;
}
.demo-nav__back {
    position: absolute;
    top: 16px;
    left: 16px;
    width: 24px;
    height: 24px;
    cursor: pointer;
}
.van-icon-wap-nav{
  line-height: 30px;
}
.mycell > div{
  font-size:14px;

}
.zhezhao{
  position: absolute;
  top:0;
  width: 100%;
  height: 100%;
  z-index:9999999;
  background-color: rgba(255, 255, 255, 0.949);
  display: flex;
  align-items: center;
  justify-content: center;
}
.zhezhao > i{
  font-size: 80px;
  color: #0000005f;
}

</style>

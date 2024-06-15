<template>
  <div >
    
      





    

  </div>
</template>

<script>
import { Notify } from 'vant';

export default {
  components: {

  },
  data() {
    return{
      websocket:null,
      retryInterval:null, //重试定时器
    }
  },
  methods:{
        /*
        1. `WebSocket.CONNECTING` (值为 0)：表示正在连接。
        2. `WebSocket.OPEN` (值为 1)：表示连接成功并且可以通信。
        3. `WebSocket.CLOSING` (值为 2)：表示连接正在关闭。
        4. `WebSocket.CLOSED` (值为 3)：表示连接已关闭或者打开连接失败。
        如果你要检查 WebSocket 是否处于打开状态，你可以使用以下方式：
        if (this.websocket.readyState === WebSocket.OPEN) {
            console.log("WebSocket is open.");
        } else {
            console.log("WebSocket is not open.");
        }

        */
        // #region 下面是socket方法
        // 初始化socket链接
        setupWebSocket () {
          if(this.$store.state.UserId){
            const protocol = process.env.VUE_APP_WEBSOCKET_URI + '/'+ this.$store.state.UserId
            this.websocket = new WebSocket(protocol) // 创建WebSocket连接
            this.websocket.onopen = this.onWebSocketOpen // WebSocket连接打开时的处理函数
            this.websocket.onerror = this.onWebSocketError //
            this.websocket.onmessage = this.onWebSocketMessage // 收到WebSocket消息时的处理函数
            this.websocket.onclose = this.onWebSocketClose // WebSocket连接关闭时的处理函数
          }
        },
        // socket成功建立后 开始发数据
        onWebSocketOpen () {
          console.log('[local]:socket链接成功')
          //this.sendMessag("hello")
        },
        onWebSocketError () {
          console.log('链接错误')
          console.log('[local]:socket链接错误')
        },
        // 接收信息函数
        onWebSocketMessage (event) {
          // json转js对象
          //const data = JSON.parse(event.data)
          //console.log(event.data)
          var objectlize = JSON.parse(event.data)
          console.log(`[local]:接收到：${objectlize}`)
          // 事件总线发出事件
          this.$root.$emit('socket_received_msg',objectlize)
        },
        onWebSocketClose (event) {
          if (this.websocket) {
            this.websocket.close() // 关闭WebSocket连接
            console.log(event)
            console.log('[local]:socket链接被本机或服务器关闭')
            this.retryWebSocketConnection()
          }
        },
        // 发送信息函数
        sendMessag (message) {
          // js转json
          message = JSON.stringify(message)
          if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
            this.websocket.send(message)
            console.log(`[local]:已发送：${message}`)
          } else {
            console.log(`[local]:发送:${message}失败`)
          }
        },
        // 重试链接函数
        retryWebSocketConnection() {
            const protocol = process.env.VUE_APP_WEBSOCKET_URI;
            // 如果已经存在一个重试定时器，先清除它
            if (this.retryInterval) {
                clearInterval(this.retryInterval);
            }
            // 每隔2秒尝试重新连接
            this.retryInterval = setInterval(() => {
              if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
                clearInterval(this.retryInterval);
                this.retryInterval = null;
                return;
              }
              Notify('socket链接已断开，重连中..');
              console.log('Attempting to reconnect WebSocket...');
              this.setupWebSocket(protocol);
            }, 2000);
        }
        // #endregion
  },
  mounted(){
    this.setupWebSocket()
  },
  watch: {
    '$store.state.UserId': {
      handler(newVal) {
        if(newVal)this.setupWebSocket()
      },
    }
  },
  beforeDestroy(){
    this.websocket.close()
  }
}
</script>

<style scoped>
.van-card{
  padding:8px;
  width:60vw;
} 
.van-card__content{
  height: 50px;
  min-height:50px;
}
.van-card__thumb{
  width:50px ;
  height: 50px;
}
.chattime{
  background-color:rgba(250, 253, 255, 0.771);
  color: #9499A0;
  margin: 10px auto;
  padding: 5px 10px;
  border-radius: 40px;
  display: inline-block;
  font-size: small;
}
.chatuser_out{
  display: flex;
  justify-content: right;
  margin-bottom: 20px;
}
.chatuser{
  font-size: small;
  background-color: aliceblue;
  overflow-wrap: break-word;
  text-align:start;
  border-radius: 10px;
  padding: 8px 10px;
  margin-right: 10px;
  max-width: 60%;
  position: relative;
}

.chatkefu_out{
  display: flex;
  justify-content: left;
  margin-bottom: 20px;
}
.chatkefu{
  font-size: small;
  background-color:white;
  overflow-wrap: break-word;
  text-align:start;
  border-radius: 10px;
  padding: 8px 10px;
  margin-left: 10px;
  max-width: 60%;
  position: relative;
}
.footer_round{
  position: absolute;
  bottom: 0;
  right: 0;
  margin: 0 5px -15px 0;
  color: #9499A0;
  font-size: smaller;
}
.footer_round2{
  position: absolute;
  bottom: 0;
  left: 0;
  margin: 0 0px -15px 5px;
  color: #9499A0;
  font-size: smaller;
}

</style>

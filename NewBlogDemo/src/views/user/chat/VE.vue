<template>
  <div style="margin: 10px;" ref="scrollContainer">
    
      <!-- 无限滚动 -->
      <van-list
      v-model="loading_vanlist"
      :finished="finished"
      @load="debouncedOnLoad"
      offset=200
      direction="up"
      >
      
      <!-- 聊天table -->
      <div v-for="(column, index) in tableData" :key="index" style="">
        <!-- 单条时间 -->
        <div v-if="column?.create_time && column?.show_time" class="chattime">{{ $formatDate(column.create_time) }}</div>
        
        <!-- 本人 -->
        <div v-if="column.user_id===user_id" class="chatuser_out">
          <div  class="chatuser">
            {{ column?.info }}
          </div>
          <van-image
            round
            width="2.5rem"
            height="2.5rem"
            :src="user?.wechat_headimgurl?user.wechat_headimgurl:require('@/assets/default_headimg5.webp')"
            :error-icon="require(`@/assets/load.webp`)"
            style="margin-right: 5px;"
            @click="$router.push(`/user/info`)"
          />
        </div>

        <!-- 对方 -->
        <div v-else class="chatkefu_out">
          <van-image
            round
            width="2.5rem"
            height="2.5rem"
            :src="receiver?.wechat_headimgurl?receiver.wechat_headimgurl:require('@/assets/default_headimg5.webp')"
            :error-icon="require(`@/assets/load.webp`)"
            style="margin-right: 5px;"
            @click="$router.push(`/user/otheruserinfo?user_id=${column.user_id}`)"
          />
          <div  class="chatkefu">
            {{ column?.info }}
          </div>
        </div>

      </div>

      </van-list>



    <sendwind ref="sendw" @sendComment="sendMessag" :is_kefuchat="true"/>
    <div v-if="$refs?.sendw?.input_disable" style="background-color: rgba(240,243,244,0.7);position: fixed;bottom:0;width: 100%;height: 50px;z-index: 99;">    
    </div>

    <rootEvent event_name="socket_received_msg" @rootEvent="reLoadOne" />

  </div>
</template>

<script>
import sendwind from '@/components/sendwind/VE.vue'
import axios from '@/utils'
import { Image as VanImage } from 'vant';
import { List} from 'vant';
import { throttle } from 'lodash';
import rootEvent from '@/components/receivedrootevent/VE.vue'

export default {
    components: {
        sendwind,
        'van-image':VanImage,
        'van-list':List,
        rootEvent
    },
    data() {
        return{
            user:{},
            user_id:null,
            receiver:null,
            receiver_id:null,
            //
            tableData: [],
            total: 0, //总个数
            current:0, // 当前页数
            pages:0, // 总页数
            loading_vanlist:false,
            finished: false,
            currentPage:0,
            PageSize:8,
            debouncedOnLoad:null
            //
        }
    },
    methods:{
        async test(){
            //await this.getMessag()
        },
        // 接收到socket消息(如果是本聊天在chat页收到 设为已读)
        async reLoadOne(data) {
            if(data.chatHistoryItemVo.last_sender_id!==this.receiver_id)return
            this.tableData.push(data.chat)
            this.sort_to_Decided_show_time()
            // 滚到底部
            this.$nextTick(() => {window.scrollTo(0, document.body.scrollHeight);})
            // 设为已读
            const chat = data.chat
            axios.post(`/chat/setIsRead`,chat)
        },
        // 发信信息后 排列决定是否显示时间
        sort_to_Decided_show_time(){
            const index = this.tableData.length -1
            var frontest_show_time
            var frontest_show_time_index
            // 一轮遍历拿到最前显示时间和索引
            for(var i=index;i>=0;i--){
                if(this.tableData[i].show_time){
                    frontest_show_time = this.tableData[i].create_time
                    frontest_show_time_index = i
                    break
                }
            }
            // 往前遍历 看有没有超10分钟 超就显示
            for(var j=index;j>frontest_show_time_index;j--){
                if(this.isOutMinutes(this.tableData[j].create_time,frontest_show_time,10)){
                    this.tableData[j].show_time = true
                    frontest_show_time = this.tableData[j].create_time
                    frontest_show_time_index = j
                }
            }
        },
        // 发送消息给receiver_id
        sendMessag(info){
            // 滚动到底部
            window.scrollTo(0, document.body.scrollHeight);
            this.$refs.sendw.input_disable = true
            axios.post(`/chat/add`,{
                info,
                receiver_id:this.receiver_id
            })
                .then(response=>{
                    if(response.data.code){
                        // 重新构造
                        console.log(response.data.data)
                        // ...
                        var ob ={
                            "id": null,
                            "user_id": this.user_id,
                            "receiver_id": null,
                            "info": info,
                            "create_time": new Date().toISOString(),
                            "show_time": false ,
                            "is_read": null,
                            "group_uuid": null
                        }
                        this.tableData.push(ob)
          
                        this.sort_to_Decided_show_time()
                        // 滚到底部
                        this.$nextTick(() => {window.scrollTo(0, document.body.scrollHeight);})
                    }
                    else {
                        this.$message.error("发送失败："+response.data.msg)
                    }

                    this.$refs.sendw.input_disable = false
                }).catch(()=>{

                    this.$refs.sendw.input_disable = false
                })
        },
        // vant手机划到顶部时触发
        async onLoad() {
            console.log("滚到顶部，触发加载")
            this.currentPage +=1
            await axios.get('chat/pagewithone',{
                params: {
                    currentPage: this.currentPage,
                    PageSize: this.PageSize,
                    receiver_id:this.receiver_id
                }
            }).then(response=>{
                if(response.data.code){
                    // 重新获取已读（因为他刚读了）
                    this.getUnreadNum()
                    //
                    this.user_id = response.data.map.user.id
                    this.user = response.data.map.user
                    this.receiver = response.data.map.receiver
                    this.$store.state.userchat_name =  this.receiver.wechat_nickname?this.receiver.wechat_nickname:this.receiver.name
                    // 记录旧位置
                    const old_window_scrollY = window.scrollY
                    const old_scrollHeight = document.body.scrollHeight
                    console.log("old_window_scrollY"+old_window_scrollY)
                    console.log("old_scrollHeight"+document.body.scrollHeight)

                    // 处理并插入新数据(show_time)
                    var mark_create_time = response.data.data.records[0].create_time
                    var newRecords = response.data.data.records.map(record => {
                        if(this.isOutMinutes(mark_create_time,record.create_time,10)){
                            mark_create_time = record.create_time
                            // 给每个元素添加新的属性
                            return {
                                ...record,
                                show_time: true // 根据需要设置新属性和值
                            };
                        }else{
                            // 给每个元素添加新的属性
                            return {
                                ...record,
                                show_time: false // 根据需要设置新属性和值
                            };
                        }
                
                    });
                    newRecords[0].show_time = true
                    // 顶插入
                    this.tableData.unshift(...newRecords);

                    // 滚回原位（只在非首次）
                    if(this.currentPage>1)this.$nextTick(() => {
                        window.scrollTo(0, document.body.scrollHeight-old_scrollHeight+old_window_scrollY)
                    })
                    // 滚到底部（只在首次）
                    if(this.currentPage===1)this.$nextTick(() => {window.scrollTo(0, document.body.scrollHeight);})
                    this.total = response.data.data.total
                    this.current = response.data.data.current
                    this.pages = response.data.data.pages
                }else{
                    this.$message.error(response.data.msg)
                }

                console.log(response)
            }).catch(error=>{
                console.log(error)
            })

            // 加载状态结束
            this.loading_vanlist = false;

            // 当前页数大于等于总页数
            //if (this.PageSize >= this.TotalPage) {
            if (this.current >= this.pages) {
                this.finished = true;
            }
        

        },
        async getUnreadNum(){
            await axios.get('/chat/getUnreadNum')
                .then(response=>{
                    if(response.data.data!=='0')this.$store.state.chat_badge = response.data.data
                    else this.$store.state.chat_badge = ""
                })
        },
        // 是否超出duration_time时间（分钟）
        isOutMinutes(time1, time2, duration_time) {
        // 将时间字符串转换为 Date 对象
            const date1 = new Date(time1);
            const date2 = new Date(time2);

            // 计算两个时间的差值（毫秒）
            const diff = Math.abs(date1.getTime() - date2.getTime());

            // 检查时间差是否超在之外
            return diff > duration_time * 60 *1000;
        },

    },
    created(){
    // 节流OnLoad 1._.throttle 每n秒钟只调用一次 2._.debounce n秒之后无操作 调用
        this.debouncedOnLoad = throttle(this.onLoad, 1000);
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.$store.state.PAGE_STATE = ""
        this.receiver_id = this.$route.query.receiver_id
        this.$store.state.userchat_id = this.receiver_id
        this.test()

    },
    beforeDestroy(){
        this.$store.state.PAGE_STATE = "Tabbar"
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
  min-height:16px;
}

.chatkefu_out{
  display: flex;
  justify-content: left;
  margin-bottom: 20px;
  margin-left: 5px;
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
  min-height:16px;
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

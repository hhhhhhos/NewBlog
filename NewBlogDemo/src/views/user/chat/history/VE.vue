<template>
  <div>
    <!-- 点赞 关注 评论 -->
    <div class="out-item">
        <div class="item" @click="$router.push(`/user/chat/history/zan?unread=${map['zan']!=='0'?true:false}`)">
            <div class="item-icon" style="background-color:rgb(250,230,229)">
                <van-icon name="good-job" size="30px" color="rgb(234,106,97)"/>
                <div v-show="map?.['zan']!=='0'" class="item-icon-unread">
                    {{ map['zan'] }}
                </div>
            </div>
            <p class="des">点赞</p>
        </div>
        <div class="item" @click="$router.push(`/user/chat/history/guanzhu?unread=${map['guanzhu']!=='0'?true:false}`)">
            <div class="item-icon" style="background-color:rgb(233,241,254)">
                <van-icon name="contact" size="30px" color="rgb(77,130,245)"/>
                <div v-show="map?.['guanzhu']!=='0'" class="item-icon-unread">
                    {{ map['guanzhu'] }}
                </div>
            </div>
            <p class="des">关注</p>
        </div>
        <div class="item" @click="$router.push(`/user/chat/history/comment?unread=${map['comment']!=='0'?true:false}`)">
            <div class="item-icon" style="background-color:rgb(229,249,240)">
                <van-icon name="comment" size="30px" color="rgb(111,213,158)"/>
                <div v-show="map?.['comment']!=='0'" class="item-icon-unread">
                    {{ map['comment'] }}
                </div>
            </div>
            <p class="des">评论</p>
        </div>
    </div>

    <!-- 无限滚动 -->
    <van-list
        v-model="loading_vanlist"
        :finished="finished"
        finished-text="没有更多了"
        @load="debouncedOnLoad"
        offset=0
        >

        <div v-for="(column, index) in tableData"
        :key="index">
          <!-- 对话框 -->
          <div class="card_out" @click="$router.push(`/user/chat?receiver_id=${column.receiver.id}`)">
            <img style="margin: auto 0;cursor: pointer;width:50px;height: 50px;border-radius: 90px;border: 2px solid white;"
            :src="column.receiver.wechat_headimgurl?column.receiver.wechat_headimgurl:require('@/assets/default_headimg5.webp')"
            @click="event => { event.stopPropagation(); $router.push(`/user/otheruserinfo?user_id=${column.receiver.id}`) }">
            <div>
              <!-- 名 -->
              <div style="text-align: start;margin: 5px 0 8px 10px;font-weight: bolder;font-size: small;">
                {{column.receiver.wechat_nickname?column.receiver.wechat_nickname:column.receiver.name}}
              </div>
              <!-- 最新一条内容 -->
              <div style="margin: 10px 0 0 10px;color: rgb(100,100,100,0.6);text-align: start;font-size: small;">
                {{column.last_info}}
              </div>
            </div>
            <!-- 日期 未读消息数-->
            <div style="margin: 5px 5px 10px 15px;margin-left: auto;color: rgb(100,100,100,0.6);font-size: small;">
              <div>{{$formatDate(column.latest_create_time)}}</div>
              <div v-if="column.unread>0 && column.last_sender_id !== $store.state.UserId"  style="margin: 10px 0px 0 0;margin-left: auto;text-align: center;
              border-radius: 20px;background-color: red;color: white;width: 20px;font-size: 12px;line-height: 18px;">
                {{column.unread}}
              </div>
            </div>
          </div>

        </div>

    </van-list>

    <rootEvent event_name="socket_received_msg" @rootEvent="reLoadOne" />

  </div>
</template>

<script>
import axios from '@/utils'
import { List} from 'vant';
import { throttle } from 'lodash';
//import { Cell } from 'vant';
import rootEvent from '@/components/receivedrootevent/VE.vue'
import { Icon } from 'vant';

export default {
    components:{
        'van-icon':Icon,
        'van-list':List,
        //'van-cell':Cell,
        rootEvent
    },
    data() {
        return{
            map:{},
            tableData: [],
            total: 0, //总个数
            current:0, // 当前页数
            pages:0, // 总页数
            loading_vanlist:false,
            finished: false,
            currentPage:0,
            PageSize:8,
            debouncedOnLoad:null
        }
    },
    methods:{
    // 接收到socket_received_msg事件 重载单组别
        async reLoadOne(data) {
            console.log(data);
            const group_uuid = data.chatHistoryItemVo.group_uuid
            for (let i = 0; i < this.tableData.length; i++) {
                if (this.tableData[i].group_uuid === group_uuid) {
                    //this.tableData[i] = data.chatHistoryItemVo; // 直接修改record.chat
                    this.$set(this.tableData, i, data.chatHistoryItemVo); // 直接更改 不会被渲染到视图 要用set？
                    break; // 找到后可以跳出循环，提升效率
                }
            }
            console.log(this.tableData);
        },
        // vant手机划到底部时触发
        async onLoad() {
            //var oldScrollPosition
            console.log("滚到底部，触发加载")
            // 异步更新数据
            // setTimeout 仅做示例，真实场景中一般为 ajax 请求
            this.currentPage +=1
            await axios.get('chat/page',{
                params: {
                    currentPage: this.currentPage,
                    PageSize: this.PageSize,
                }
            }).then(response=>{
                if(response.data.code){
                    this.tableData.splice(this.tableData.length, 0, ...response.data.data.records);
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
        getUnreadZanGuanzhuComment(){
            axios.get('action/count',{
            }).then(response=>{
                if(response.data.code){
                    this.map = response.data.data
                }else{
                    this.$message.error(response.data.msg)
                }
            }).catch(error=>{
                console.log(error)
            })
        }
    },
    mounted(){
        this.getUnreadZanGuanzhuComment()
        this.$store.state.zhezhao_show = false
    },
    created(){
    // 节流OnLoad 1._.throttle 每n秒钟只调用一次 2._.debounce n秒之后无操作 调用
        this.debouncedOnLoad = throttle(this.onLoad, 1000);
    }
}
</script>

<style scoped>
.card_out{
  background-color: white;
  margin: 8px 0 0 0;
  padding: 10px 10px;
  display:flex;
  cursor: pointer;
}

.out-item{
    display: flex;
    margin:10px;
    margin-top: 20px;
}
.item{
    width:33%;
    cursor: pointer;
}
.item-icon{
    position: relative;
    margin: 0 auto;
    border-radius: 20px;
    height: 60px;
    width: 60px;
    display: flex;
    justify-content: center;
    align-items: center;
}
.item-icon-unread{
    position: absolute;
    top:-8px;
    right:-6px;
    margin: 10px 0px 0 0;
    margin-left: auto;
    text-align: center;
    border-radius: 20px;
    background-color: red;
    color: white;
    width: 20px;
    font-size: 12px;
    line-height: 18px;
}
.des{
    margin: 8px auto;
    font-size: 0.9rem;
}
</style>

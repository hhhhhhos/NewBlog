<template>
  <div style="margin: 10px;" v-loading="test_loading">
    <!-- 电脑 -->
    <div v-if="false">

    </div>

     <!-- 手机 -->
     <div v-else>
      
      <!-- 聊天table -->
      <div v-for="(column, index) in chat_tabledata" :key="index" style="">
        <!-- 单条时间 -->
        <div v-if="column.show_time" class="chattime">{{ formatMessageDate(column.time) }}</div>
        <!-- user -->
        <div v-if="column.role==='user'" class="chatuser_out">
          <!-- 最新一条发送失败 显示 -->
          <i v-if="fail_index_list.indexOf(index)!==-1" style="display: flex;align-items: center;margin-right: 5px;color: red;font-weight: bolder;" class="el-icon-warning-outline"></i>
          <div  class="chatuser">
            {{ column.info }}
            <span class="footer_round">{{ `${index+1}/${10+fail_index_list.length}`}}</span>
          </div>
          <van-image
            round
            width="2.5rem"
            height="2.5rem"
            :src=target_img_src
            :error-icon="require(`@/assets/load.webp`)"
            style="margin-right: 5px;"
          />
        </div>

        <!-- assistant -->
        <div v-if="column.role==='assistant'" class="chatkefu_out">
          <van-image
            round
            width="2.5rem"
            height="2.5rem"
            :src="require(`@/assets/kefu.png`)"
            :error-icon="require(`@/assets/load.webp`)"
            style="margin-left: 5px;"
          />
          <div  class="chatkefu">
            {{ column.info }}
            <div v-if="column?.product_list?.length>0" >
              <van-card

                style="cursor: pointer;"
                :desc="column?.product_list[0]?.info"
                :title="column?.product_list[0]?.name"
                :thumb="column?.product_list[0]?.photo_url?column.product_list[0].photo_url:column?.product_list[0]?.photo===undefined?require(`@/assets/noproduct.webp`):require(`@/assets/${column?.product_list[0]?.photo}.webp`)"
                @click="$router.push(`/product?id=${column?.product_list[0]?.id}`)"
              >
              </van-card>
            </div>
            <span class="footer_round2">{{ `${index+1}/${10+fail_index_list.length}`}}</span>
          </div>
        </div>

      </div>
      
      <!-- 是否结束聊天 -->
      <div class="chattime" v-if="is_end">
          聊天已结束
      </div>


    </div>

    <sendwind ref="sendw" @sendComment="sendComment" :is_kefuchat="true" :show_photo_icon="false"/>
    <div v-if="$refs?.sendw?.input_disable" style="background-color: rgba(240,243,244,0.7);position: fixed;bottom:0;width: 100%;height: 50px;z-index: 99;">    
    </div>

  </div>
</template>

<script>
import moment from 'moment'
import sendwind from '@/components/sendwind/VE.vue'
import axios from '@/utils'
import { Image as VanImage } from 'vant';
import { Card } from 'vant';

export default {
    components: {
        sendwind,
        'van-image':VanImage,
        'van-card':Card,
    },
    data() {
        return{
            // 对话是否结束
            is_end:false,
            // 发送失败的index
            fail_index_list:[],
            // 对话id
            chat_id:null,
            // test初始化加载
            test_loading:true,
            // 前端渲染对象
            message_object:{
                time:null,
                show_time:true,
                role:null,
                info:null
            },
            // 传给后端对象
            OpenAiJsonMessageObject:{
                role:"user",
                content:null,
            },
            chat_tabledata:[],
            chat_tabledata_test:[
                {
                    "time":"2024-05-13T05:01:00",
                    "show_time":false,
                    "role":"user",
                    "info":"你好你好你好你好你好你好你好你好你好你好你好你好"
                },
                {
                    "time":"2024-05-13T05:11:00",
                    "show_time":false,
                    "role":"user",
                    "info":"在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子"
                },
                {
                    "time":"2024-05-13T05:21:00",
                    "show_time":false,
                    "role":"user",
                    "info":"在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子"
                },
                {
                    "time":"2024-05-13T05:31:00",
                    "show_time":false,
                    "role":"user",
                    "info":"在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子"
                },
                {
                    "time":"2024-05-13T05:41:00",
                    "show_time":false,
                    "role":"user",
                    "info":"在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子在吗在吗小傻子"
                },
                {
                    "time":"2024-05-13T06:51:00",
                    "show_time":false,
                    "role":"assistant",
                    "info":"不在，傻逼子在吗在吗小傻子在吗在吗小傻子在~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                }
            ],
            ii:0,
            target_img_src:require('@/assets/default_headimg5.webp'),
            obj:null
        }
    },
    methods:{
    // 初始化
        async test(){
            await this.getuserinfo()
            console.log(this.$route.params.segment1)
            if(this.$route.query.chat_id===undefined)await this.getchat_history_list(-1)
            else await this.getchat_history_list(this.$route.query.chat_id)
        },
        // 发送axios GET请求 gpt某个单聊天
        async getchat_history_list(id){
            await axios.get(`/kefu/chat?chat_id=${id}`)
                .then(response=>{
                    if(response.data.code){
                        if(response.data.data === null)this.chat_tabledata = []
                        else{
                            // 如果有历史消息 构造前端页面显示
                            response.data.data.forEach(item=>{
                                this.build_message_object(item.role,item.content,item.create_time)
                            })
                            // 是否结束对话
                            this.is_end = response.data.map.is_finish
                            if(this.is_end)this.$refs.sendw.input_disable = true
                            // 赋值chat_id
                            this.chat_id = response.data.map.chat_id
                            // 赋值客服名字
                            this.$store.state.kefu_name = response.data.map.kefu_name
                        }
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                })
        },
        // 为了获取微信头像
        async getuserinfo(){
            await axios.get('/user/info')
                .then(response=>{
                    if(response.data.code){
                        this.obj = response.data.data
                        if(this.obj?.wechat_headimgurl !== null){
                            this.target_img_src = this.obj.wechat_headimgurl
                        }
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                })
        },
        // 构造前端显示体 并添加到列表 参数1 "user" 或者 "assistant" 2 评论内容
        build_message_object(role,comment_info,create_time){
            console.log("create_time!!:"+create_time)
            // 新增评论构造无时间？
            if(!create_time)create_time = this.NowDate();
            // 包含商品表
            if(comment_info.includes(":find_product:")){
                const index = comment_info.indexOf(":find_product:")
                comment_info = comment_info.substring(index+(":find_product:".length))
                console.log(comment_info)
                const product_list = JSON.parse(comment_info)
                // 创建一个新的消息对象
                let new_message_object = {
                    time: create_time,
                    role: role,
                    info: product_list.length===0?"啊哦，没找到哦":"你要找的是下面这些文章吗",
                    product_list:product_list
                };
                // 加到显示列表
                this.chat_tabledata.push(new_message_object)
            }else{
                // 创建一个新的消息对象
                let new_message_object = {
                    time: create_time,
                    role: role,
                    info: comment_info
                };
                // 加到显示列表
                this.chat_tabledata.push(new_message_object)
            }
        
      
            this.time_show(this.chat_tabledata.length-1)// 这里不用赋值 会自动赋值time_show到chat_tabledata 一定要在列表push之后
            return this.chat_tabledata.length -1
        },
        // 评论按下发送触发
        sendComment(comment_info){     
            // 构造前端显示对象
            const index = this.build_message_object("user",comment_info)
      
            // 发送axios请求
            this.sendComment_axios(comment_info,index)

            this.$refs.sendw.comment_info = ""
        },
        // 发送axios POST请求获取gpt回答
        sendComment_axios(comment_info,index){
            // 滚动到底部
            setTimeout(() => {window.scroll({top: document.body.scrollHeight,left: 0,behavior: 'smooth'})}, 0);
            this.$refs.sendw.input_disable = true
            this.OpenAiJsonMessageObject.content = comment_info
            axios.post(`/kefu/chat?chat_id=${this.chat_id}`,this.OpenAiJsonMessageObject)
                .then(response=>{
                    if(response.data.code){
                        // 构造前端显示对象
                        console.log(response.data.data.content)
                        this.build_message_object("assistant",response.data.data.content)
                        // 是否结束对话
                        this.is_end = response.data.map.is_finish
                        // 赋值chat_id
                        this.chat_id = response.data.map.chat_id
                        // 赋值客服名字
                        this.$store.state.kefu_name = response.data.map.kefu_name
                        // 滚动到底部
                        window.scrollTo(0, document.body.scrollHeight);
                    }
                    else {
                        this.$message.error("发送失败："+response.data.msg)
                        this.fail_index_list.push(index)
                        if(response.data.msg==="此轮对话已结束")setTimeout(() => {window.location.reload()}, 500);
                    }

                    this.$refs.sendw.input_disable = false
                }).catch(()=>{
                    this.fail_index_list.push(index)

                    this.$refs.sendw.input_disable = false
                })
        },
        // 是否显示time
        time_show(index){
            // 第一项时间要显示
            if(index===0)this.chat_tabledata[index].show_time = true;
            const itime = this.chat_tabledata[index].time
            for(var i=index-1; i>=this.ii ; i--){
                console.log("index:"+index+"第"+i+"次遍历")
                if(this.isOutMinutes(this.chat_tabledata[i].time,itime,30)){
                    console.log(this.ii+"被赋值"+index)
                    this.ii = index
                    this.chat_tabledata[index].show_time = true
                }
            }
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
        // 返回形如'2023-04-11T05:01:00'的当前时间
        NowDate(){
            // 创建一个新的 Date 对象，默认为当前日期和时间
            const now = new Date();

            // 转换为 ISO 格式字符串
            const isoString = now.toISOString();
            return isoString
        },
        // 整理为消息发送时间
        formatMessageDate(date) {
            const now = moment(); // 使用 moment.utc() 获取当前时间，并转换为 UTC 时间
            const givenDate = moment(date); // 使用 moment.utc() 将给定的时间转换为 UTC 时间
            console.log("givenDate:" + givenDate.format('MM月DD日 HH:mm') + " date:" + date);
            const diffDays = now.diff(givenDate, 'days');

            if (diffDays === 0) {
                // 今天
                return givenDate.local().format('HH:mm'); // 使用 local() 方法将 UTC 时间转换为本地时间
            } else if (diffDays === 1) {
                // 昨天
                return `昨天 ${givenDate.local().format('HH:mm')}`;
            } else if (now.isoWeek() === givenDate.isoWeek()) {
                // 本周内
                return `${givenDate.local().format('dddd HH:mm')}`;
            } else if (now.year() === givenDate.year()) {
                // 今年内但已超过一周
                return givenDate.local().format('MM月DD日 HH:mm');
            } else {
                // 超过一年
                return givenDate.local().format('YYYY年MM月DD日 HH:mm');
            }
        }

    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.$store.state.PAGE_STATE = ""
        // 初始化
        this.test().then(()=>{
            // 初始化是否显示时间
            for(var i=0;i<this.chat_tabledata.length;i++){
                this.time_show(i)
            }
            this.test_loading = false
        })
    
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

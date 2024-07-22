<template>
  <div class="my-container">
    
    <div style="position: relative;z-index: 0;background-color: rgb(54, 170, 237);">
      <div style="display:flex;padding: 70px 0 0px 0;">

        <!-- 左边 头像.ID 归属地 -->
        <div style="display: flex; flex-direction: column;">
            <img
              style="z-index:999;margin: 0px 0px 5px 25px;z-index: 2;cursor: pointer;width:70px;height: 70px;border-radius: 90px;border: 2px solid white;background-color: white;"
              :src="obj?.wechat_headimgurl?obj.wechat_headimgurl:target_img_src"
              @click="$openURL2(`${obj?.wechat_headimgurl?obj.wechat_headimgurl:target_img_src}`)"
            >
          <div class="name" style="z-index: 1;margin: 0 0 0 25px; display: flex; flex-direction: column; ">
              <div style="font-weight: bold;font-size: small;position: relative;">{{obj?.wechat_nickname?obj.wechat_nickname:this.obj?.name?this.obj.name:"用户不存在"}}<img style="position: absolute;right: -30;" v-if="obj?.wechat_nickname" width="18px" height="18px" :src="require('@/assets/wechat_confirm.png')"></div>
              <div style="font-size:11px;">ID:{{this.obj?.id?this.obj.id:"未知"}}</div>
              <div>
                <span style="font-size: 11px;" >
                  <i class="el-icon-location-outline"></i>
                  {{this?.obj?.ip_location?this.obj.ip_location:"公共厕所"}}
                </span>
              </div>
              <div style="font-size:xx-small;">{{ $FormDate(this?.obj?.last_visited_time) }}</div>
          </div>

        </div>

        <!-- 右边 粉丝动态 关注 发起聊天-->
        <div style="display: flex; flex-direction: column;width: 100%;">
          
          <div style="display: flex;justify-content: right;">
            <el-row style="width: 100%;max-width:400px;margin: 15px 30px 10px 10px;z-index: 1;">
              <el-col :span="8">
                <p v-html="dt"></p>
                <span>动态</span>
              </el-col>
              <el-col :span="8">
                <p @click="$router.push(`/user/fanorguanzhu/fs/${user_id}`)"
                v-html="fs===-1?'<i class=\'el-icon-loading\'></i>':fs"></p>
                <span>粉丝</span>
              </el-col>
              <el-col :span="8">
                <p @click="$router.push(`/user/fanorguanzhu/gz/${user_id}`)"
                v-html="gz===-1?'<i class=\'el-icon-loading\'></i>':gz"></p>
                <span>关注</span>
              </el-col>
            </el-row>
          </div>
          
          <div style="display: flex;justify-content: right;">
            <el-button @click="addordeleteFan()" :loading="addordeleteFan_loading" size="small" :type="addordeleteFan_type" round style="z-index: 1;width:70px;height:min-content;margin: 15px 0 0 0;">
              {{addordeleteFan_text}}
            </el-button>
            <el-button @click="user_id === $store.state.UserId?$fail('不能和自己聊天'):$router.push(`/user/chat?receiver_id=${obj?.id}`)" round style="margin:15px 18px 0 10px;padding: 7px 11px;z-index:1;height:min-content;">
              <i style="z-index:1;font-size:15px"  class="el-icon-chat-dot-round"></i>
            </el-button>
            </div>

        </div>

      </div>

      <div style="border-top-left-radius: 10px;border-top-right-radius: 10px;position: absolute;top:85px;height: 185px;width: 100%;background-color: white;z-index:0;">
      </div>
    </div>

    <div class="item" style="margin: 40px 0px 5px 30px;">
      
      <div style="font-weight: bold;font-size: 12px;position: relative;">
        详细信息
      </div>

      <div style="height: 10px;">

      </div>
      
      <div class="item_in" v-for="(column, index) in this.obj" :key="index">
        <div v-if="index!=='last_visited_time' && index!=='version' &&index!=='id' && index!=='addresses' && index!=='password' && index!=='wechat_unionid' && index!=='wechat_headimgurl' && index!=='wechat_nickname'" style="display: flex;">
          <div style="width: 100px;">{{trans_index[index]}}:</div><span>{{ index==='create_time'?column.replace('T',' '):column }}</span>
        </div>
      </div>

    </div>

  </div>
</template>

<script>
import { Toast } from 'vant';
import { Dialog } from 'vant';
import axios from '@/utils'

export default {
    name: 'MyIndex',
    components: {
    },
    props: {},
    data () {
        return {
            addordeleteFan_text:"关注",
            addordeleteFan_type:"primary",
            addordeleteFan_loading:false,
            dt:0,
            fs:-1,
            gz:-1,
            obj:null,
            target_img_src:require('@/assets/load.webp'),
            user_id:null,
            trans_index:{
                "name":"姓名",
                "age":"年龄",
                "sex":"性别",
                "phone":"电话",
                "create_time":"创建时间",
                "role":"角色",
                "money":"余额",
                "ip_location":"归属地",
            }
        }
    },
    methods: {
        async test(){
            await this.getuserinfo()
            await this.getfaninfo()
        },
        async getuserinfo(){

            await axios.get(`/user/otheruserinfo?user_id=${this.user_id}`)
                .then(response=>{
                    if(response.data.code)this.isloading1=false
                    else this.$message.error("获取失败："+response.data.msg)
                    this.obj = response.data.data
                    if(this.obj?.wechat_headimgurl === null){
                        this.target_img_src = require('@/assets/default_headimg5.webp')
                    }
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
        async getfaninfo(){

            await axios.get(`/fan/info?user_id=${this.user_id}`)
                .then(response=>{
                    if(response.data.code){
                        // 如果response.data.data未true表示已关注
                        if(response.data.data){
                            this.addordeleteFan_text = "已关注"
                            this.addordeleteFan_type = "info"
                        }
                        this.gz = response.data.map.gz
                        this.fs = response.data.map.fs
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })

        },
        addordeleteFan(){
            if(this.addordeleteFan_text==="已关注"){
                Dialog.confirm({
                    title: '',
                    message: '取消关注？'
                })
                    .then(() => {
                        // 点击确认执行的代码
                        this.addordeleteFan_axios()
              
                    })
                    .catch(() => {
                        // 点击取消执行的代码
                    });

            }else{
                this.addordeleteFan_axios()
            }
        },
        addordeleteFan_axios(){
      
            this.addordeleteFan_loading=true
            axios.post(`/fan/addordelete?user_id=${this.user_id}`)
                .then(response=>{
                    if(response.data.code){
                        // this..
                        Toast.success(response.data.data);
                        if(this.addordeleteFan_text==="关注"){
                            this.addordeleteFan_text = "已关注"
                            this.addordeleteFan_type = "info"
                        }
                        else{
                            this.addordeleteFan_text = "关注"
                            this.addordeleteFan_type = "primary"
                        }
                        this.getfaninfo()
                    }
                    else Toast.fail(response.data.msg)
                    this.addordeleteFan_loading=false
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                    this.addordeleteFan_loading=false
                })
        },
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.user_id = this.$route.query.user_id
        this.test()
    }
}
</script>

<style scoped>
.my-container{
  background-color: white;
  width: 100%;
  height: 100vh;
  position: absolute;
}
.name > div{
  text-align: left;
  margin: 5px 0 0 5px;
  font-size: 14px;
}
.el-col > span{
  margin: 0;
  font-size: smaller;
}
.el-col > p{
  margin: 15px 0 5px 0;
  font-weight: bold;
}
.item > div{
  text-align: left;
  margin-bottom: 10px;
}

.item > .item_in{
  font-size: small;
  
}
.el-col > p{
  cursor: pointer;
}

</style>

<template>
  <div class="my-container">
    <div class="in-container">
    
      <div v-loading="loading" style="position: relative;z-index: 0;background-color: rgb(54, 170, 237);border-radius: 18px;">
        <div style="display:flex;padding: 70px 0 0px 0;">

          <!-- 左边 头像.ID 归属地 -->
          <div style="display: flex; flex-direction: column;">
              <img
                style="z-index:999;margin: 0px 0px 5px 25px;z-index: 2;width:70px;height: 70px;border-radius: 90px;border: 2px solid white;background-color: white;"
                :src="obj?.wechat_headimgurl?obj.wechat_headimgurl:target_img_src"

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



        </div>

        <div style="border-top-left-radius: 10px;border-top-right-radius: 10px;position: absolute;top:85px;height: 185px;width: 100%;background-color: white;z-index:0;">
        </div>
      </div>

      <div v-loading="loading" class="item" style="margin: 40px 0px 5px 30px;">
        
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
  </div>
</template>

<script>
import axios from '@/utils/axios';

export default {
    name: 'MyIndex',
    components: {
    },
    props: {},
    data () {
        return {
            loading:true,
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
        },
        async getuserinfo(){
            await axios.get('/user/session')
                .then(response=>{
                    console.log(response.data)
                    for(const str of response.data){
                        console.log(str)
                        if(str.startsWith('LoginName'))
                            this.name = str.slice(9)
                        if(str.startsWith('IsLogin'))
                            this.$store.state.app.UserId =  str.slice(8)
                    }

                }).catch(error=>{
                    this.$message.error("error")
                    console.log(error)
                })

            await axios.get(`/user/otheruserinfo?user_id=${this.$store.state.app.UserId}`)
                .then(response=>{
                    if(response.data.code)this.isloading1=false
                    else this.$message.error("获取失败："+response.data.msg)
                    this.obj = response.data.data
                    if(this.obj?.wechat_headimgurl === null){
                        this.target_img_src = require('@/assets/default_headimg5.webp')
                    }
                    this.loading = false
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
    },
    mounted(){
        this.test()
    }
}
</script>

<style scoped>
.my-container{
  width: 100%;
  height:80vh;
  padding-top: 30px;
  position: absolute;
  display: flex;
  justify-content: center;
  align-items: center;
}
.in-container{
  width:390px;
  background-color: white;
  height: 100%;
  margin: 30px;
  border-radius: 20px;
  border: 2px solid #49506068;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
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

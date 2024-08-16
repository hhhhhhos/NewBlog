<template>
  <div>

    <div v-if="false">
      <h1 class="myborder">用户信息</h1>

      <img v-lazy style="height:80px;margin: 15px 0 0px 0 ;border-radius: 66px;" :src="obj.wechat_headimgurl?obj.wechat_headimgurl:target_img_src">
      
      <div v-loading="isloading1" class="myborder" style="margin: 30px 100px 20px 100px;padding: 10px;">
        <div class="myhover" v-for="(data,index) in this.obj" :key="index" style="text-align: left;display: flex;">
          <div v-if="!isupdateinfo" style="margin: 10px 90px;display: flex;">
              <div v-if="index==='addresses'" style="display: flex;">
                <div style="width: 150px;flex-shrink: 0;">{{ index }}</div>:
                  <div>
                    <div style="margin:0 20px 20px 20px;" v-for="(data2,index2) in data" :key="index2">
                      {{ index2+1 }}. | 所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp; | 详细地址：{{ data2.detail }} | 收件人：{{ data2.name }} | 电话：{{ data2.phone }} <div v-if="data2.is_default" style="margin: 5px 0 0 30px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div>
                    </div>
                  </div>
              </div>
              <div v-else-if="index==='wechat_headimgurl'" style="display: flex;">
                <div style="width: 150px;display: flex;align-items: center;">{{ index }}</div><span style="display: flex;align-items: center;">:</span><img style="margin:0 20px ;border-radius: 66px;" :src="data">
              </div>
              <div v-else style="display: flex;">
                <div style="width: 150px;">{{ index }}</div>:<div style="margin:0 20px ;">{{ index==="create_time"?data.replace("T"," " ):data }}</div>
              </div>
            </div>
          <div v-else style="margin: 5px 90px;display: flex;">
            <div style="width: 150px;">{{ index }}</div>:
            <div style="margin:0px 20px ;">
              <el-input v-if="index!='id'&& index!='ip_location' && index!='create_time'&&index!='role'&&index!='addresses'&&index!='money'&&index!='version'&&index!='wechat_nickname'&&index!='wechat_unionid'&&index!='wechat_headimgurl'&&index!='email'" v-model="obj[index]" size="mini" placeholder="请输入内容"></el-input>
              <div v-else-if="index==='addresses'">
                <div style="margin:10px 20px 20px 10px;" v-for="(data2,index2) in data" :key="index2">
                  {{ index2+1 }}. | 所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp;| 详细地址：{{ data2.detail }} | 收件人：{{ data2.name }} | 电话：{{ data2.phone }} <div v-if="data2.is_default" style="margin: 5px 0 0 30px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div><el-button @click="change_address(index2,data2)" style="margin-left: 20px;" size="mini" type="primary" icon="el-icon-edit" circle></el-button><el-button size="mini" @click="delete_address(index2)" type="danger" icon="el-icon-delete" circle></el-button>
                </div>
                <el-button @click="add_address" style="margin-left: 20px;" size="mini" type="primary" icon="el-icon-edit">新增地址</el-button>
              </div>
              <el-input v-else v-model="obj[index]" :disabled="true" size="mini" placeholder="请输入内容"></el-input>
            </div>
          </div>
        </div>
      </div>
      <el-button v-if="!isupdateinfo" type="primary" @click="isupdateinfo=true">修改</el-button>
      <el-button v-else type="primary" @click="updateuserinfo">提交</el-button>
      <el-button v-if="isupdateinfo" type="primary" @click="cancel">取消</el-button>
      <el-button type="primary" @click="logout" v-if="false" >退出登录</el-button>
      <el-dialog :title="this.dialog_title+this.dialogindex" :visible.sync="dialogVisible" width="40%" >
        <el-form ref="form" :model="dialogdata" label-width="80px" >
          <el-form-item label="所在地区">
            <ElA ref="ELA" style="float:left;" :PselectedOptions.sync="dialogdata.info_code" @info="info=>{dialogdata.info=info}" @info_code="info_code=>{dialogdata.info_code=info_code}"></ElA>
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="dialogdata.detail"></el-input>
          </el-form-item>
          <el-form-item label="收件人">
            <el-input v-model="dialogdata.name"></el-input>
          </el-form-item>
          <el-form-item label="电话号码">
            <el-input v-model="dialogdata.phone"></el-input>
          </el-form-item>
          <el-form-item label="是否默认">
            <el-switch
              v-model="dialogdata.is_default"
              active-text="是"
              inactive-text="否"
              style="float:left;margin-top: 10px;">
            </el-switch>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirm_change_address">确定</el-button>
        </span>
      </el-dialog>
      <div style="height: 50px;"></div>
    </div>

    <div v-else>
      
        <headimg 
      the_style="height:80px;margin: 15px 0 5px 0 ;border-radius: 66px;"  
      :target_img_src="target_img_src" 
      :obj="obj" />
      
      <div v-loading="isloading1" class="myborder" style="padding: 10px; width: 90vw;margin: 8px auto 15px;">
        <div class="myhover" v-for="(data,index) in this.obj" :key="index" style="text-align: left;display: flex;">
          
          
          <div class="info" v-if="!isupdateinfo" style="display: flex;font-size: small;">
              <div v-if="index==='addresses'" style="display: flex;">
                <div style="width: 100px;flex-shrink: 0;">{{ trans_index[index] }}</div>:
                  <div>
                    <div style="margin:0 20px 20px 20px;" v-for="(data2,index2) in data" :key="index2">
                      <div class="div-spacing">
                        <div>
                        所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp;
                        </div>
                        <div>详细地址：{{ data2.detail }} </div>
                        <div>收件人：{{ data2.name }} </div>
                        <div>电话：{{ data2.phone }} </div>
                        <el-button @click="change_address(index2,data2)" style="margin-left: 0px;" size="mini" type="primary" icon="el-icon-edit" circle></el-button><el-button size="mini" @click="delete_address(index2)" type="danger" icon="el-icon-delete" circle></el-button>
                        <div v-if="data2.is_default" style="margin: 5px 0 0 15px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div>
                      </div>
                    </div>
                  </div>
              </div>
              <div v-else-if="index==='password'" style="display: flex;">
                <div style="width: 100px;">{{ trans_index[index] }}</div>:<div style="margin:0 20px ;">哈希码</div>
              </div>
              
              <div v-else-if="index==='wechat_headimgurl'" style="display: none;">
                
              </div>

              <div v-else style="display: flex;">
                <div style="width: 100px;">{{ trans_index[index] }}</div>:<div style="margin:0 20px ;">{{ index==="create_time"?data?.replace("T"," "):data }}</div>
              </div>
          </div>


          <div v-else style="margin: 5px 5px;display: flex;font-size: small;">
            <div style="width: 100px;">{{ trans_index[index] }}</div>:
            <div style="margin:0px 20px ;">
              <el-input v-if="index!='id' && index!='ip_location'  &&index!='create_time'&&index!='role'&&index!='addresses'&&index!='money'&&index!='version'&&index!='wechat_nickname'&&index!='wechat_unionid'&&index!='wechat_headimgurl' " v-model="obj[index]" size="mini" placeholder="请输入内容"></el-input>
              <div v-else-if="index==='addresses'">
                <div style="margin:0 20px 20px 20px;" v-for="(data2,index2) in data" :key="index2">
                  <div class="div-spacing">
                    <div>
                    所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp;
                    </div>
                    <div>详细地址：{{ data2.detail }} </div>
                    <div>收件人：{{ data2.name }} </div>
                    <div>电话：{{ data2.phone }} </div>
                    <el-button @click="change_address(index2,data2)" style="margin-left: 0px;" size="mini" type="primary" icon="el-icon-edit" circle></el-button><el-button size="mini" @click="delete_address(index2)" type="danger" icon="el-icon-delete" circle></el-button>
                    <div v-if="data2.is_default" style="margin: 5px 0 0 15px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div>
                  </div>
                </div>
                <el-button @click="add_address" style="margin-left: 20px;" size="mini" type="primary" icon="el-icon-edit">新增地址</el-button>
              </div>
              <el-input v-else v-model="obj[index]" :disabled="true" size="mini" placeholder="请输入内容"></el-input>
            </div>
          </div>
          
        </div>
      </div>
      <el-button v-if="!isupdateinfo" type="primary" @click="isupdateinfo=true">修改</el-button>
      <el-button v-else type="primary" @click="updateuserinfo">提交</el-button>
      <el-button v-if="isupdateinfo" type="primary" @click="cancel">取消</el-button>
      <el-button v-if="false"  type="primary" @click="logout">退出登录</el-button>
      
      <el-dialog :title="this.dialog_title+this.dialogindex" :visible.sync="dialogVisible" width="80vw" >
        <el-form ref="form" :model="dialogdata" label-width="80px" >
          <el-form-item label="所在地区">
            <ElA ref="ELA" style="float:left;" :PselectedOptions.sync="dialogdata.info_code" @info="info=>{dialogdata.info=info}" @info_code="info_code=>{dialogdata.info_code=info_code}"></ElA>
          </el-form-item>
          <el-form-item label="详细地址">
            <el-input v-model="dialogdata.detail"></el-input>
          </el-form-item>
          <el-form-item label="收件人">
            <el-input v-model="dialogdata.name"></el-input>
          </el-form-item>
          <el-form-item label="电话号码">
            <el-input v-model="dialogdata.phone"></el-input>
          </el-form-item>
          <el-form-item label="是否默认">
            <el-switch
              v-model="dialogdata.is_default"
              active-text="是"
              inactive-text="否"
              style="float:left;margin-top: 10px;">
            </el-switch>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirm_change_address">确定</el-button>
        </span>
      </el-dialog>
    </div>

  </div>
</template>

<script>
import axios from '@/utils'

import ElA from '/src/components/ElAddress'
import headimg from '@/components/headimg/VE.vue'

export default {
    components: {
        ElA,
        headimg
    },
    data() {
        return{
            trans_index:{
                "id":"ID",
                "name":"姓名",
                "age":"年龄",
                "sex":"性别",
                "email":"邮箱",
                "blog":"博客",
                "phone":"电话",
                "create_time":"创建时间",
                "role":"角色",
                "money":"余额",
                "addresses":"收货地",
                "ip_location":"归属地",
                "password":"密码",
                "wechat_nickname":"微信名",
                "wechat_headimgurl":"微信头像",
            },
            target_img_src:require('@/assets/load.webp'),
            isloading1:true,
            datas:[],
            obj:{
                "id": null,
                "name": null,
                "age": null,
                "sex": null,
                "addresses": null,
                "phone": null,
                "create_time": Date,
                "password": null,
                "money":null
            },
            isupdateinfo:false,
            dialogVisible:false,
            dialog_title:"修改地址",
            dialogdata:
      {
          "info": "",
          "name": "",
          "phone": "",
          "detail": "",
          "is_default":""
      },
            dialogindex:1
        }
    },
    methods:{
        getuserinfo(){
            axios.get('/user/info')
                .then(response=>{
                    if(response.data.code)this.isloading1=false
                    else this.$message.error("获取失败："+response.data.msg)
                    this.obj = response.data.data
                    if(this.obj.addresses===null) this.obj.addresses=[]
                    else{
                        if(response.data.data?.wechat_headimgurl!==null){
                            this.target_img_src = response.data.data?.wechat_headimgurl
                        }else{
                            this.target_img_src = require('@/assets/default_headimg1.webp')
                        }
                        // 默认地址置顶
                        for(var i=0;i<this.obj.addresses.length;i++){
                            if(this.obj.addresses[i].is_default){
                                const temp = this.obj.addresses[0]
                                this.obj.addresses[0] = this.obj.addresses[i]
                                this.obj.addresses[i] = temp
                            }
                        }
                    }
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
        // 点修改
        async updateuserinfo(){
            //if(this.obj.age!=null&&(this.obj.age>200||this.obj.age<0))return this.$message.error("年龄不合法")
            //if(this.obj.sex!=null&&(this.obj.sex!="男"&&this.obj.sex!="女"))return this.$message.error("性别不合法")
            //if (!(/^1[3|4|5|6|7|8][0-9]\d{8}$/.test(this.obj.phone)))return this.$message.error("电话不合法")
            var result = false
            await axios.put('/user/update',this.obj)
                .then(response=>{
                    if(response.data.code){
                        this.$message.success(response.data.data)
                        this.isloading1 = true
                        this.isupdateinfo = false
                        this.$store.state.UserName = this.obj.name
                        this.getuserinfo()
                        result = true
                    }
                    else this.$message.error("修改失败："+response.data.msg)   
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("修改失败："+error)
                })
            return result
        },
        // 点取消
        cancel(){
            this.isupdateinfo=false
            this.$message.error("已取消修改")
            this.getuserinfo()
        },
        logout(){
            axios.get('/user/logout')
                .then(response=>{
                    console.log(response)
                    if(response.data.code){
                        this.$message.success("退出登录成功")
                        this.$store.state.IsLogin = false
                        this.$router.push('/home')
                    }
                    else this.$message.error("退出失败："+response.data.msg)

                }).catch(error=>{
                    console.log(error)
                    this.$message.error("错误："+error.data.msg)
                })
        },
        // 点击修改圆圈
        change_address(index2,data2){
            this.dialog_title = "修改地址"
            this.dialogindex = index2+1
            this.dialogdata = Object.assign({},data2) // 浅拷贝
            this.dialogVisible = true
        },
        // 点击展开框的确认
        confirm_change_address(){
            if(this.dialogdata.is_default){
                this.obj.addresses.forEach(address=>address.is_default = false)
            }
            //this.dialogdata.info_code = 
            //this.$refs.ELA.selectedOptions
            this.obj.addresses[this.dialogindex-1] = Object.assign({},this.dialogdata) // 浅拷贝
            this.dialogVisible = false
            //if(true){
            this.updateuserinfo()
                .then(response=>{
                    if(response){
                        this.$message.success("已"+this.dialog_title+(this.dialogindex))
                    }
                })

            //}else{

            //}
        
        },
        add_address(){
            this.dialog_title = "新增地址"
            this.dialogVisible = true
            this.dialogindex = this.obj.addresses.length + 1
            this.dialogdata = {}
        },
        delete_address(index2){
            this.$confirm('删除地址'+(index2+1)+'?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                var adr_before = JSON.parse(JSON.stringify(this.obj.addresses)) // 备份 删除失败复位
                this.obj.addresses.splice(index2,1) // 删除数组下标为index2的，后面的数前移
                this.updateuserinfo()
                    .then(response=>{
                        if(response)this.$message.success("删除成功")
                        else this.obj.addresses = JSON.parse(JSON.stringify(adr_before)) // 删除失败复位
                    })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });          
            });
        }
    },
    mounted(){
        this.$store.state.zhezhao_show = false
        this.getuserinfo()
    },
    watch:{
    }
}
</script>

<style scoped>
.myhover:hover{
  background-color: var(--el-loading-mask-bg);
}
.div-spacing > div {
  margin-bottom: 10px;
}
.info > div{
  margin: 5px 5px;
}
</style>

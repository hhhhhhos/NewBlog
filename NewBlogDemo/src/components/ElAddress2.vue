<template>
  <div v-loading="isloading1">

    <div v-if="!this.$store.state.IsMobile">
      <el-radio-group v-model="radio" style="margin:10px 20px 20px 10px;display: block;" v-for="(data2,index2) in this.obj.addresses" :key="index2">
        <el-radio :label="index2">
          {{ index2+1 }}. | 所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp;| 详细地址：{{ data2.detail }} | 收件人：{{ data2.name }} | 电话：{{ data2.phone }} <div v-if="data2.is_default" style="margin: 5px 0 0 30px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div><el-button @click="change_address(index2,data2)" style="margin-left: 20px;" size="mini" type="primary" icon="el-icon-edit" circle></el-button><el-button size="mini" @click="delete_address(index2)" type="danger" icon="el-icon-delete" circle></el-button>
        </el-radio>
      </el-radio-group>
      <el-button @click="add_address" style="margin-left: 60px;display: block;" size="mini" type="primary" icon="el-icon-edit">新增地址</el-button>
      <!-- 弹出框 -->
      <el-dialog :title="this.dialog_title+this.dialogindex" :visible.sync="dialogVisible" width="40%" >
        <el-form ref="form" :model="dialogdata" label-width="80px" >
          <el-form-item label="所在地区">
            <ElA style="float:left;" :PselectedOptions="dialogdata.info_code" @info="info=>{dialogdata.info=JSON.parse(JSON.stringify(info))}" @info_code="info_code=>{dialogdata.info_code=JSON.parse(JSON.stringify(info_code))}"></ElA>
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

    <div v-else>
      <el-radio-group v-model="radio" style="margin:10px 20px 20px 10px;display: block;" v-for="(data2,index2) in this.obj.addresses" :key="index2">
        <el-radio :label="index2" style="display: flex;">
          <div class="div-spacing">
            <div>
            所在地区：{{ data2.info?.[0] }} / {{ data2.info?.[1] }} / {{ data2.info?.[2] }} &nbsp;
            </div>
            <div>详细地址：{{ data2.detail }} </div>
            <div>收件人：{{ data2.name }} </div>
            <div>电话：{{ data2.phone }} </div>
            <div v-if="data2.is_default" style="margin: 5px 0 0 5px;display: inline-block;background-color: rgba(127, 255, 212, 0.648);border: 1px rgba(0, 0, 0, 0.171) solid;border-radius: 5px;padding: 5px;color: rgb(14, 198, 249);">默认</div><el-button @click="change_address(index2,data2)" style="margin-left: 20px;" size="mini" type="primary" icon="el-icon-edit" circle></el-button><el-button size="mini" @click="delete_address(index2)" type="danger" icon="el-icon-delete" circle></el-button>
          </div>
        </el-radio>
      </el-radio-group>
      <el-button @click="add_address" style="margin-left: 10px;display: block;" size="mini" type="primary" icon="el-icon-edit">新增地址</el-button>
      <!-- 弹出框 -->
      <el-dialog :title="this.dialog_title+this.dialogindex" :visible.sync="dialogVisible" width="80%" >
        <el-form ref="form" :model="dialogdata" label-width="80px" >
          <el-form-item label="所在地区">
            <ElA style="float:left;" :PselectedOptions="dialogdata.info_code" @info="info=>{dialogdata.info=JSON.parse(JSON.stringify(info))}" @info_code="info_code=>{dialogdata.info_code=JSON.parse(JSON.stringify(info_code))}"></ElA>
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
// 主要用在buylist
import axios from '@/utils'

import ElA from '/src/components/ElAddress'

export default {
    components: {
        ElA
    },
    data() {
        return{
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
                "password": null
            },
            isupdateinfo:false,
            dialogVisible:false,
            dialog_title:"修改地址",
            dialogdata:
      {
          "info": "",
          "info_code":"",
          "name": "",
          "phone": "",
          "detail": "",
          "is_default":""
      },
            dialogindex:1,
            radio:0
        }
    },
    methods:{
        getuserinfo(){
            axios.get('/user/info')
                .then(response=>{
                    if(response.data.code){
                        this.isloading1=false
                        this.obj = response.data.data
                        if(this.obj.addresses===null) this.obj.addresses=[]
                        else{
                            // 默认地址置顶
                            for(var i=0;i<this.obj.addresses.length;i++){
                                if(this.obj.addresses[i].is_default){
                                    const temp = {...this.obj.addresses[0]}// 深拷贝
                                    this.obj.addresses[0] = {...this.obj.addresses[i]}// 深拷贝
                                    this.obj.addresses[i] = {...temp}// 深拷贝
                                }
                            }
                            // 传给buylist_result address
                            this.$emit("address",this.obj.addresses)
                            this.$emit("radio",this.radio)
                        }
                    }
                    else this.$message.error("获取失败："+response.data.msg)
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("获取失败："+error.data.msg)
                })
        },
        async updateuserinfo(){
            //if(this.obj.age!=null&&(this.obj.age>200||this.obj.age<0))return this.$message.error("年龄不合法")
            //if(this.obj.sex!=null&&(this.obj.sex!="男"&&this.obj.sex!="女"))return this.$message.error("性别不合法")
            //if (!(/^1[3|4|5|6|7|8][0-9]\d{8}$/.test(this.obj.phone)))return this.$message.error("电话不合法")
            await axios.put('/user/update',this.obj)
                .then(response=>{
                    if(response.data.code){
                        this.$message.success(response.data.data)
                        this.isloading1 = true
                    }
                    else this.$message.error("修改失败："+response.data.msg)   
                }).catch(error=>{
                    console.log(error)
                    this.$message.error("修改失败："+error)
                })
            this.getuserinfo()
        },
        logout(){
            axios.get('/user/logout')
                .then(response=>{
                    console.log(response)
                    if(response.data.code){
                        this.$message.success("退出登录成功")
                        this.$store.state.IsLogin = false
                        this.$router.push('/xiba_shop/home')
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
            this.dialogdata = {...data2} // 深拷贝
            this.dialogVisible = true
        },
        // 点击展开框的确认
        confirm_change_address(){
            if(this.dialogdata.is_default){
                this.obj.addresses.forEach(address=>address.is_default = false)
            }
            this.obj.addresses[this.dialogindex-1] = {...this.dialogdata} // 深拷贝
            //this.$message.success("已"+this.dialog_title+(this.dialogindex))
            this.dialogVisible = false
            this.updateuserinfo()
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
                this.obj.addresses.splice(index2,1) // 删除数组下标为index2的，后面的数前移
                this.$message({
                    type: 'success',
                    message: '删除成功!'
                });
                this.updateuserinfo()
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
    // 守望者，未登陆不能访问本页
        '$store.state.IsLogin':function(){
            if(!this.$store.state.IsLogin)this.$router.push('/xiba_shop/home')
        },
        isupdateinfo:function(newv){
            if(!newv){
                this.$message.error("已取消修改")
                this.getuserinfo()
            }
        },
        radio:function(){
            this.$emit("radio",this.radio)
            this.$emit("address",this.obj.addresses)
        }
    }
}
</script>

<style scoped>
.myhover:hover{
  background-color: rgb(247,248,255);
}
.div-spacing > div {
  margin-bottom: 10px;
}
</style>

<style>
.el-message-box {
  max-width: 100vw !important; /* 视口宽度 */
}
</style>

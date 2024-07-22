<template>
    <div class="show">
        <van-uploader v-if="false" v-model="fileList" multiple />
        <!-- 当前路径 -->
        <div style="display: flex;">
            <p>当前路径</p>
            <i class="el-icon-arrow-left myback" @click="removeLastPartAndGet"></i>
            <input v-model="path" 
            @keyup.enter="getphotoall"
            style="margin: 10px 0 0 10px;width: 300px;height: 2rem;" />
            <el-button @click="deletelistphoto" style="height: 32px;margin: 10px 10px 0 10px;display: flex;justify-content: center;align-items: center;">
                删除
            </el-button>
            <el-button @click="postnewfolder" style="height: 32px;margin: 10px 10px 0 10px;display: flex;justify-content: center;align-items: center;">
                新建文件夹
            </el-button>
            <el-button @click="addnewphotointhisurl" style="height: 32px;margin: 10px 10px 0 10px;display: flex;justify-content: center;align-items: center;">
                上传
            </el-button>
            <input type="file" id="imageInput" accept="image/*" style="margin: 15px 10px 0 10px;"> 
        </div>

        <!-- 图片展示框 -->
        <div v-loading="ploading" 
        style="display: flex; flex-wrap: wrap;height: 70vh;overflow: scroll;background-color: white;">
            <div class="photo" v-for="(column, index) in this.photo_list" :key="index">
                <!-- 图 -->
                <div v-if="column.name.endsWith('.webp') || column.name.endsWith('.jpg') || column.name.endsWith('.png')">
                    <el-image style="cursor: pointer;" 
                    lazy
                    :preview-src-list="[photo_forward_url + column.name]"
                    :title=column.name class="img" :src="photo_forward_url + column.name" />
                    <p :title=column.name>{{column.name}}</p>
                    <p>{{column.size}}</p>
                </div>
                <!-- 其他文件 -->
                <div v-else-if="column.name.includes('.')">
                    <el-image style="cursor: pointer;" 
                    :preview-src-list="[photo_forward_url + column.name]" 
                    :title=column.name class="img" :src="photo_forward_url + 'unknown_icon.png'" />
                    <p :title=column.name>{{column.name}}</p>
                    <p>{{column.size}}</p>
                </div>
                <!-- 文件夹 -->
                <div v-else>
                    <i class="el-icon-folder-opened img folder"
                    style="cursor: pointer;font-size: 150px;color: bisque;"
                    @click="path.endsWith('/')?(path = path + column.name,getphotoall()):(path = path + '/' + column.name,getphotoall())"
                    :title=column.name >
                    </i>
                    <p :title=column.name>{{column.name}}</p>
                    <p> &nbsp;</p>
                </div>
                <input type="checkbox" v-model=column.is_selected>
            </div>
        </div>
    </div>
  </template>
  
<script>
import { Uploader } from 'vant';
import axios from '@/utils/axios';

export default {
    components:{
        'van-uploader':Uploader,
    },
    data() {
        return{
            fileList: [
                { url: 'https://img01.yzcdn.cn/vant/leaf.jpg' },
                // Uploader 根据文件后缀来判断是否为图片文件
                // 如果图片 URL 中不包含类型信息，可以添加 isImage 标记来声明
                { url: 'https://cloud-image', isImage: true },
            ],
            photo_list:[],
            photo_forward_url:process.env.VUE_APP_STATIC_PATH,
            path:'/',
            ploading:true
        }
    },
    methods:{
        addnewphotointhisurl(){

        },
        postnewfolder(){
            this.$prompt('请输入文件夹名（或文件夹路径）', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
            }).then(({ value }) => {
                axios.post(`/photo/folderbyadmin?path=${this.path}/${value}`)
                    .then(res=>{
                        if(res.data){
                            this.$message.success("成功创建")
                            this.getphotoall()
                        }
                        else this.$message.error("创建失败")
                    }).catch(err=>{
                        this.$message.error(err)
                    })
            }).catch(() => {     
            });
        },
        deletelistphoto(){
            const delete_list = []
            this.photo_list.forEach(item=>{
                if(item.is_selected)delete_list.push(this.path.endsWith('/')?
                    this.path + item.name : this.path + '/' + item.name)
            })
            if(!delete_list.length) return this.$message.error("未选择图片")

            this.$confirm('确定删除这'+(delete_list.length)+'个图吗?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                axios.delete('/photo/listbyadmin',{data:delete_list})
                    .then(response=>{
                        this.$message(response.data)
                        this.getphotoall()
                    })
                    .catch(error=>{
                        this.$message.error(error)
                    })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });          
            });
        },
        removeLastPartAndGet(){
            if(this.path === '/') return this.$message.error("已是顶格")
            //找到最后一个斜杠的位置
            const lastSlashIndex = this.path.lastIndexOf('/');
            
            if (lastSlashIndex !== -1 && lastSlashIndex !== 0) {
                this.path = this.path.substring(0, lastSlashIndex);
            }else{
                this.path = '/'
            }

            this.getphotoall()
        },
        getphotoall(){
            this.ploading = true
            axios.get('/photo/allbyadmin?path='+this.path)
                .then(response=>{
                    if(response.data.code){
                        this.photo_list = response.data.data
                        this.photo_list.forEach(photo => {
                            photo.is_selected = false
                        });
                        this.ploading = false
                    }
                    else{
                        this.$message.error(response.data.msg)
                        this.photo_list = []
                    }

                })
        }
    },
    mounted(){
        this.getphotoall()
    }
}
</script>

<style scoped>
.myback{
    margin: 13px -3px 13px 5px;
    padding: 5px;
    font-weight: 800;
    cursor: pointer;
    border-radius: 15px;
}
.myback:hover{
    background-color: white;
}
.show{
    margin: 30px;
}
.photo{
    margin: 10px;
}
.img{
    width: 150px;
    height: 150px;
}
p{
        /* 设置最大宽度 */  
        max-width: 150px;  
        /* 设置溢出内容为隐藏 */  
        overflow: hidden;  
        /* 设置文本溢出时显示省略号 */  
        text-overflow: ellipsis;  
        /* 确保文本在一行内显示，这样溢出时才会显示省略号 */  
        white-space: nowrap;  
}
</style>
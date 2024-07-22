<template>
  <div>
    <vue-editor
      id="editor"
      v-model="content" style="background-color: white;"
    >
    </vue-editor>
    <el-button  type="primary" plain @click="send" style="margin-top: 10px;">send</el-button>

    <div class="quillWrapper" style="background-color: white;display: none;">
      <div class="ql-container ql-snow">
        <div v-html="content2" class="ql-editor" data-gramm="false" contenteditable="false">
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import { VueEditor } from "vue2-editor";
import axios from '@/utils/axios';

export default {

    components: {
        VueEditor
    },

    data() {
        return {
            content: "",
            content2:""
        };
    },

    methods:{
        send(){
            this.$emit("send",this.content)
        },
        handleImageAdded(file, Editor, cursorLocation, resetUploader) {
            // An example of using FormData
            // NOTE: Your key could be different such as:
            // formData.append('file', file)
            console.log("img add!!!")

            var formData = new FormData();
            formData.append("photo", file);

            axios({
                url: "/product/saveTempPhotobyadmin",
                method: "POST",
                data: formData
            })
                .then(result => {
                    if(result.data.code){
                        const url = process.env.VUE_APP_STATIC_PATH + result.data.data; // Get url from response
                        Editor.insertEmbed(cursorLocation, "image", url);
                        resetUploader();
                    }else{
                        this.$message.error(result.data.msg)
                    }

                })
                .catch(err => {
                    console.log(err);
                    this.$message.error(err)
                });
        }
    },
    beforeDestroy(){
        axios({
            url: "/product/deleteTempPhotobyadmin",
            method: "delete", // 注意这里使用小写的 "delete"
        })
    },
    beforeCreate(){
        axios({
            url: "/product/deleteTempPhotobyadmin",
            method: "delete", // 注意这里使用小写的 "delete"
        })
    }
};
</script>
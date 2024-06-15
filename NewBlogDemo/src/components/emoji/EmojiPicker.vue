<template>
  <div class="emoji-picker" style="background-color: rgb(243, 243, 244);">
    <div v-for="emoji in emojis" :key="emoji" @click="selectEmoji(emoji)">
      {{ emoji }}
    </div>
  </div>
</template>

<script>
import emojis from './emoji-compact.json';

export default {
  name: 'EmojiPicker',
  data() {
    return {
      emojis
    };
  },
  methods: {
    selectEmoji(selectedEmoji) {
      this.$emit('emoji-selected', selectedEmoji);
    }
  }
};
</script>

<style>
.emoji-picker div {
  cursor: pointer;
  display: inline-block;
  margin: 0;  /* 移除外边距 */
  padding: 4px; /* 适当的内边距，可根据需求调整 */
  font-size: 24px;
}


</style>

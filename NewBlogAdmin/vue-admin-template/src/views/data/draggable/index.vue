<template>
    <div
      ref="draggable"
      :style="styleObject"
      @mousedown="startDrag"
      @mousemove="onDrag"
      @mouseup="endDrag"
      @mouseleave="endDrag"
      class="draggable"
    >
      <slot></slot>
    </div>
</template>
  
<script>
export default {
    data() {
        return {
            dragging: false,
            styleObject: {
                position: 'absolute',
                left: '0px',
                top: '0px',
            },
            initialX: 0,
            initialY: 0,
            offsetX: 0,
            offsetY: 0,
        };
    },
    methods: {
        startDrag(event) {
            this.dragging = true;
            this.initialX = event.clientX - this.offsetX;
            this.initialY = event.clientY - this.offsetY;
        },
        onDrag(event) {
            if (this.dragging) {
                event.preventDefault();
                this.offsetX = event.clientX - this.initialX;
                this.offsetY = event.clientY - this.initialY;
                this.styleObject.left = `${this.offsetX}px`;
                this.styleObject.top = `${this.offsetY}px`;
            }
        },
        endDrag() {
            this.dragging = false;
        },
    },
};
</script>
  
  <style>
  .draggable {
    cursor: grab;
  }
  .draggable:active {
    cursor: grabbing;
  }
  </style>
  
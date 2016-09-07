Welcome to the GGSDoc wiki!

利用该插件，可以进行对java entity 添加get set 方法的同时，生成注释；

注意：注释必须按照严格的java doc格式，否则可能报错

使用前，如下图，

    /**
     * 活动ID
     */
    private Long id;

使用之后，只需一键操作，即可，

    /**
     * 设置活动ID
     *
     * @param id 活动ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取活动ID
     *
     * @return 活动ID id
     */
    public Long getId() {
        return id;
    }

# SweetMessages

消息相关的基础命令插件。支持 [MiniMessage格式](https://docs.advntr.dev/minimessage/format.html) 以及 PAPI 变量

PAPI 变量默认不替换，自行在命令选项添加 `-p=true` 来替换变量。

根命令为 `/sweetmessages`，别名为 `/smsgs` 或 `/smsg`  
使用类 Linux 系统的命令格式。

## 聊天消息
子命令 `message`, `msg`, `m`
```
/sweetmessages message <接收者选择器> [选项] <消息文字>
在接收者的聊天栏输出一条消息

接收者选择器:
  @a, @e            发送给所有在线玩家
  @s                发送给命令发送者
  @p                发送给距离自己最近的玩家，控制台不可使用该选择器
  @r                随机在线玩家
  玩家名            发送给指定玩家
  玩家1,玩家2,玩家3  发送给指定的多个玩家

选项:
  -p,--papi,--placeholder,--placeholders=<布尔值>    是否替换PAPI变量
  -d,--delay=<时间>                                  延时多少tick后再发送消息

消息文字:
  输入需要发送给接收者的文字，使用\n可换行

```

## ActionBar 消息
子命令 `actionbar`, `action`, `a`
```
/sweetmessages actionbar <接收者选择器> [选项] <消息文字>
在接收者的物品栏上方输出一条消息

接收者选择器:
  @a, @e            发送给所有在线玩家
  @s                发送给命令发送者
  @p                发送给距离自己最近的玩家，控制台不可使用该选择器
  @r                随机在线玩家
  玩家名            发送给指定玩家
  玩家1,玩家2,玩家3  发送给指定的多个玩家

选项:
  -p,--papi,--placeholder,--placeholders=<布尔值>    是否替换PAPI变量
  -d,--delay=<时间>                                  延时多少tick后再发送消息

消息文字:
  输入需要发送给接收者的文字

```

## 标题消息
子命令 `title`, `t`
```
/sweetmessages title <接收者选择器> [选项] <标题文字>
在接收者的屏幕中间输出标题消息

接收者选择器:
  @a, @e            发送给所有在线玩家
  @s                发送给命令发送者
  @p                发送给距离自己最近的玩家，控制台不可使用该选择器
  @r                随机在线玩家
  玩家名            发送给指定玩家
  玩家1,玩家2,玩家3  发送给指定的多个玩家

选项:
  -p,--papi,--placeholder,--placeholders=<布尔值>    是否替换PAPI变量
  -d,--delay=<时间>                                  延时多少tick后再发送消息
  -t,--time=<时间/预设名称>                           设置标题消息的淡入、保持、淡出时间，
                                                     单位为tick，使用英文逗号分隔三个数值
                                                     或者使用配置文件中预设的值
  -i,--fade-in,--fadeIn,--fade_in=<时间>             设置标题消息的淡入时间，单位为tick
  -s,--stay=<时间>                                   设置标题消息的保持时间，单位为tick
  -o,--fade-out,--fadeOut,--fade_out=<时间>          设置标题消息的淡出时间，单位为tick

标题文字:
  输入需要发送给接收者的标题文字，默认为主标题，可用 主标题\n副标题 的格式添加副标题

```

# SimpleItemBuilder
A simple API class to create complicated ItemStacks with olny one line of Code
> For Spigot 1.8



# How to use?
* Just download the Item class [here](https://github.com/alberteisetin/SimpleItemBuilder/archive/refs/heads/main.zip) and copy it into your project
* done!

# Features
* Create banners
* Use player and custom skulls
* Hide enchantments
* Set items unbreakable
* Brew custom potions
* Use custom enchantments
* ...

# Examples
> ```java 
> // Create a simple potato
> ItemStack item = new Item(Material.POTATO_ITEM).build();
> ```
> ![potato](https://user-images.githubusercontent.com/45802535/128598533-435a5af9-6636-4fa9-a6ce-e99a7a792850.png)

> ```java
> // Let's give the potato a name
> ItemStack funnyItem = new Item(Material.POTATO_ITEM).setDisplayname("§6Funny potato").build();
> ```
> ![funnyPotato](https://user-images.githubusercontent.com/45802535/128598644-2e7db9ff-c0d0-448c-9ff8-23b18181c51d.png)

> ```java
> // A player head
> ItemStack playerHead = new Item(Material.SKULL_ITEM, (short) 3, Item.ItemMeta.ItemMeta).setOwner("alberteistein").build();
> ```
> ![playerHead](https://user-images.githubusercontent.com/45802535/128598647-1a5eced0-f820-4093-a2db-0daf300f526d.png)

> ```java
> // And whatever this is
> ItemStack customHead = new Item("723863981895b104c1b29c9f5f427ae0a0ede464584587068fb1593a27d").build();
> ```
> ![customHead](https://user-images.githubusercontent.com/45802535/128598650-69bf17be-aae9-482a-91d3-f2a9b059056b.png)

> ```java
> // Here's something more complicated...
> ItemStack fast = new Item(Material.GOLD_PICKAXE).setDisplayname("§b§o§k..§e§l Fast §b§o§k..")
>         .setLore("§6§lSwoooooshhhh", "", "§7Efficiency §7§k99999")
>         .addEnchantment(Enchantment.DIG_SPEED, 10)
>         .hideFlags()
>         .setUnbreakable()
>         .build();
> ```
> ![fast](https://user-images.githubusercontent.com/45802535/128598653-2f452617-00c9-4acf-a393-6957f1bd1f87.gif)

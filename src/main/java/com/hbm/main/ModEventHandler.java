package com.hbm.main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.missile.EntityMissileBaseAdvanced;
import com.hbm.entity.mob.EntityNuclearCreeper;
import com.hbm.entity.projectile.EntityBurningFOEQ;
import com.hbm.entity.projectile.EntityMeteor;
import com.hbm.handler.ArmorUtil;
import com.hbm.handler.VersionChecker;
import com.hbm.items.ModItems;
import com.hbm.items.armor.ArmorFSB;
import com.hbm.lib.Library;
import com.hbm.lib.ModDamageSource;
import com.hbm.lib.RefStrings;
import com.hbm.packet.PacketDispatcher;
import com.hbm.packet.RadSurveyPacket;
import com.hbm.saveddata.AuxSavedData;
import com.hbm.saveddata.RadiationSavedData;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent.EnteringChunk;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

public class ModEventHandler
{	
	public static int meteorShower = 0;
	static Random rand = new Random();
	
	@SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		
		System.out.println("eeeeee");
		
        if(!event.player.worldObj.isRemote) {
        	event.player.addChatMessage(new ChatComponentText("Loaded world with Hbm's Nuclear Tech Mod " + RefStrings.VERSION + " for Minecraft 1.7.10!"));
        	
        	if(VersionChecker.newVersion) {
        		event.player.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "New version " + VersionChecker.versionNumber + " is available!"));
        	}
        }
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		
		event.entityLiving.getEntityData().setFloat("hfr_radiation", 0);
		
		if(event.entity.worldObj.isRemote)
			return;
		
		if(MainRegistry.enableCataclysm) {
			EntityBurningFOEQ foeq = new EntityBurningFOEQ(event.entity.worldObj);
			foeq.setPositionAndRotation(event.entity.posX, 500, event.entity.posZ, 0.0F, 0.0F);
			event.entity.worldObj.spawnEntityInWorld(foeq);
		}
		
		if(event.entity.getUniqueID().toString().equals(Library.HbMinecraft)) {
			event.entity.dropItem(ModItems.book_of_, 1);
		}
		
		if(event.entity instanceof EntityEnderman && event.source == ModDamageSource.boxcar) {
			
			for(Object o : event.entity.worldObj.playerEntities) {
				EntityPlayer player = (EntityPlayer)o;
				
				if(player.getEntityData().getFloat("hfr_radiation") > 250 && player.isBurning()) {
					player.triggerAchievement(MainRegistry.bobHidden);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void spawnMob(LivingSpawnEvent event) {
		EntityLivingBase entity = event.entityLiving;
		World world = event.world;

		if(entity instanceof EntityZombie) {
			if(rand.nextInt(64) == 0)
				entity.setCurrentItemOrArmor(4, new ItemStack(ModItems.gas_mask_m65, 1, world.rand.nextInt(100)));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(4, new ItemStack(ModItems.gas_mask, 1, world.rand.nextInt(100)));
			if(rand.nextInt(256) == 0)
				entity.setCurrentItemOrArmor(4, new ItemStack(ModItems.mask_of_infamy, 1, world.rand.nextInt(100)));
			
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.pipe_lead, 1, world.rand.nextInt(100)));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.reer_graar, 1, world.rand.nextInt(100)));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.pipe_rusty, 1, world.rand.nextInt(100)));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.crowbar, 1, world.rand.nextInt(100)));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.geiger_counter, 1));
			if(rand.nextInt(128) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.steel_pickaxe, 1, world.rand.nextInt(300)));
			if(rand.nextInt(512) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.stopsign));
			if(rand.nextInt(512) == 0)
				entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.sopsign));
		}
		if(entity instanceof EntitySkeleton) {
			if(rand.nextInt(16) == 0) {
				entity.setCurrentItemOrArmor(4, new ItemStack(ModItems.gas_mask_m65, 1, world.rand.nextInt(100)));
				
				if(rand.nextInt(32) == 0) {
					entity.setCurrentItemOrArmor(0, new ItemStack(ModItems.syringe_poison));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void worldTick(WorldTickEvent event) {
		
		/////
		//try {
		/////
		
		
		/// METEOR SHOWER START ///
		if(event.world != null && !event.world.isRemote && event.world.provider.isSurfaceWorld() && MainRegistry.enableMeteorStrikes) {
			if(event.world.rand.nextInt(meteorShower > 0 ? MainRegistry.meteorShowerChance : MainRegistry.meteorStrikeChance) == 0) {
				if(!event.world.playerEntities.isEmpty()) {
					EntityPlayer p = (EntityPlayer)event.world.playerEntities.get(event.world.rand.nextInt(event.world.playerEntities.size()));
					
					if(p != null && p.dimension == 0) {
						EntityMeteor meteor = new EntityMeteor(event.world);
						meteor.posX = p.posX + event.world.rand.nextInt(201) - 100;
						meteor.posY = 384;
						meteor.posZ = p.posZ + event.world.rand.nextInt(201) - 100;
						meteor.motionX = event.world.rand.nextDouble() - 0.5;
						meteor.motionY = -2.5;
						meteor.motionZ = event.world.rand.nextDouble() - 0.5;
						event.world.spawnEntityInWorld(meteor);
					}
				}
			}
			
			if(meteorShower > 0) {
				meteorShower--;
				if(meteorShower == 0 && MainRegistry.enableDebugMode)
					MainRegistry.logger.info("Ended meteor shower.");
			}
			
			if(event.world.rand.nextInt(MainRegistry.meteorStrikeChance * 100) == 0 && MainRegistry.enableMeteorShowers) {
				meteorShower = 
						(int)(MainRegistry.meteorShowerDuration * 0.75 + 
								MainRegistry.meteorShowerDuration * 0.25 * event.world.rand.nextFloat());

				if(MainRegistry.enableDebugMode)
					MainRegistry.logger.info("Started meteor shower! Duration: " + meteorShower);
			}
		}
		/// METEOR SHOWER END ///

		/// RADIATION STUFF START ///
		if(event.world != null && !event.world.isRemote && MainRegistry.enableRads) {
			
			int thunder = AuxSavedData.getThunder(event.world);
			
			if(thunder > 0)
				AuxSavedData.setThunder(event.world, thunder - 1);
			
			if(!event.world.loadedEntityList.isEmpty()) {

				RadiationSavedData data = RadiationSavedData.getData(event.world);
				
				if(data.worldObj == null) {
					data.worldObj = event.world;
				}
				
				for(Object o : event.world.playerEntities) {
					
					if(o instanceof EntityPlayerMP) {
						EntityPlayerMP player = (EntityPlayerMP)o;
						PacketDispatcher.wrapper.sendTo(new RadSurveyPacket(player.getEntityData().getFloat("hfr_radiation")), player);
					}
				}
				
				if(event.world.getTotalWorldTime() % 20 == 0) {
					data.updateSystem();
				}
				
				List<Object> oList = new ArrayList<Object>();
				oList.addAll(event.world.loadedEntityList);
				
				for(Object e : oList) {
					if(e instanceof EntityLivingBase) {
						
						//effect for radiation
						EntityLivingBase entity = (EntityLivingBase) e;

						if(event.world.getTotalWorldTime() % 20 == 0) {

							Chunk chunk = entity.worldObj.getChunkFromBlockCoords((int)entity.posX, (int)entity.posZ);
							float rad = data.getRadNumFromCoord(chunk.xPosition, chunk.zPosition);
							
							if(event.world.provider.isHellWorld && MainRegistry.hellRad > 0 && rad < MainRegistry.hellRad)
								rad = MainRegistry.hellRad;
							
							if(rad > 0) {
								Library.applyRadData(entity, rad / 2);
							}
							
							if(entity.worldObj.isRaining() && MainRegistry.cont > 0 && AuxSavedData.getThunder(entity.worldObj) > 0 &&
									entity.worldObj.canBlockSeeTheSky(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ))) {

								Library.applyRadData(entity, MainRegistry.cont * 0.005F);
							}
						}
						
						float eRad = entity.getEntityData().getFloat("hfr_radiation");
						
						if(entity instanceof EntityCreeper && eRad >= 200 && entity.getHealth() > 0) {
							
							if(event.world.rand.nextInt(3) == 0 ) {
								EntityNuclearCreeper creep = new EntityNuclearCreeper(event.world);
								creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
				        		
				        		if(!entity.isDead)
				        			if(!event.world.isRemote)
				        				event.world.spawnEntityInWorld(creep);
				        		entity.setDead();
							} else {
								entity.attackEntityFrom(ModDamageSource.radiation, 100F);
							}
							continue;
		        		
			        	} else if(entity instanceof EntityCow && !(entity instanceof EntityMooshroom) && eRad >= 50) {
			        		EntityMooshroom creep = new EntityMooshroom(event.world);
			        		creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);

			        		if(!entity.isDead)
			        			if(!event.world.isRemote)
			        				event.world.spawnEntityInWorld(creep);
			        		entity.setDead();
							continue;
			        		
			        	} else if(entity instanceof EntityVillager && eRad >= 500) {
			        		EntityZombie creep = new EntityZombie(event.world);
			        		creep.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
			        		
			        		if(!entity.isDead)
				        		if(!event.world.isRemote)
				        			event.world.spawnEntityInWorld(creep);
			        		entity.setDead();
							continue;
			        	}
						
						if(eRad < 200 || entity instanceof EntityNuclearCreeper || entity instanceof EntityMooshroom || entity instanceof EntityZombie || entity instanceof EntitySkeleton)
							continue;
						
						if(entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode)
							continue;
						
						if(eRad > 2500)
							entity.getEntityData().setFloat("hfr_radiation", 2500);
						
						if(eRad >= 1000) {
							if(entity.attackEntityFrom(ModDamageSource.radiation, entity.getMaxHealth() * 100)) {
								entity.getEntityData().setFloat("hfr_radiation", 0);

					        	if(entity instanceof EntityPlayer)
					        		((EntityPlayer)entity).triggerAchievement(MainRegistry.achRadDeath);
							}
							
							//.attackEntityFrom ensures the recentlyHit var is set to enable drops.
							//if the attack is canceled, then nothing will drop.
							//that's what you get for trying to cheat death
				        	entity.setHealth(0);
				        	
						} else if(eRad >= 800) {
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 5 * 30, 0));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 10 * 20, 2));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 10 * 20, 2));
				        	if(event.world.rand.nextInt(500) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.poison.id, 3 * 20, 2));
				        	if(event.world.rand.nextInt(700) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.wither.id, 3 * 20, 1));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 5 * 20, 3));
							
						} else if(eRad >= 600) {
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 5 * 30, 0));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 10 * 20, 2));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 10 * 20, 2));
				        	if(event.world.rand.nextInt(500) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.poison.id, 3 * 20, 1));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 3 * 20, 3));
							
						} else if(eRad >= 400) {
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 5 * 30, 0));
				        	if(event.world.rand.nextInt(500) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 5 * 20, 0));
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 5 * 20, 1));
				        	if(event.world.rand.nextInt(500) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 3 * 20, 2));
				        	
						} else if(eRad >= 200) {
				        	if(event.world.rand.nextInt(300) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.confusion.id, 5 * 20, 0));
				        	if(event.world.rand.nextInt(500) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.weakness.id, 5 * 20, 0));
				        	if(event.world.rand.nextInt(700) == 0)
				            	entity.addPotionEffect(new PotionEffect(Potion.hunger.id, 3 * 20, 2));
				        	
				        	if(entity instanceof EntityPlayer)
				        		((EntityPlayer)entity).triggerAchievement(MainRegistry.achRadPoison);
						}
					}
				}
			}
		}
		/// RADIATION STUFF END ///
	}
	
	@SubscribeEvent
	public void onEntityAttacked(LivingAttackEvent event) {
		
		EntityLivingBase e = event.entityLiving;

		if(e instanceof EntityPlayer && ArmorUtil.checkArmor((EntityPlayer)e, ModItems.euphemium_helmet, ModItems.euphemium_plate, ModItems.euphemium_legs, ModItems.euphemium_boots)) {
			e.worldObj.playSoundAtEntity(e, "random.break", 5F, 1.0F + e.getRNG().nextFloat() * 0.5F);
			event.setCanceled(true);
		}
		
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e;

			ItemStack helmet = player.inventory.armorInventory[3];
			ItemStack plate = player.inventory.armorInventory[2];
			ItemStack legs = player.inventory.armorInventory[1];
			ItemStack boots = player.inventory.armorInventory[0];
			
			if(plate != null && plate.getItem() instanceof ArmorFSB) {
				
				ArmorFSB chestplate = (ArmorFSB)plate.getItem();
				
				boolean noHelmet = chestplate.noHelmet;
			
				if((helmet != null || noHelmet) && plate != null && legs != null && boots != null) {
					
					if((noHelmet || chestplate.getArmorMaterial() == ((ItemArmor)helmet.getItem()).getArmorMaterial()) &&
						chestplate.getArmorMaterial() == ((ItemArmor)legs.getItem()).getArmorMaterial() &&
						chestplate.getArmorMaterial() == ((ItemArmor)boots.getItem()).getArmorMaterial()) {
						
						if(chestplate.fireproof && event.source.isFireDamage()) {
							player.extinguish();
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamaged(LivingHurtEvent event) {
		
		EntityLivingBase e = event.entityLiving;
		
		if(e instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e;

			ItemStack helmet = player.inventory.armorInventory[3];
			ItemStack plate = player.inventory.armorInventory[2];
			ItemStack legs = player.inventory.armorInventory[1];
			ItemStack boots = player.inventory.armorInventory[0];
			
			if(plate != null && plate.getItem() instanceof ArmorFSB) {
				
				ArmorFSB chestplate = (ArmorFSB)plate.getItem();
				
				boolean noHelmet = chestplate.noHelmet;
			
				if((helmet != null || noHelmet) && plate != null && legs != null && boots != null) {
					
					if((noHelmet || chestplate.getArmorMaterial() == ((ItemArmor)helmet.getItem()).getArmorMaterial()) &&
						chestplate.getArmorMaterial() == ((ItemArmor)legs.getItem()).getArmorMaterial() &&
						chestplate.getArmorMaterial() == ((ItemArmor)boots.getItem()).getArmorMaterial()) {
						
						if(chestplate.damageMod != -1) {
							event.ammount *= chestplate.damageMod;
						}
						
						if(chestplate.resistance.get(event.source.getDamageType()) != null) {
							event.ammount *= chestplate.resistance.get(event.source);
						}
						
						if(chestplate.blastProtection != -1 && event.source.isExplosion()) {
							event.ammount *= chestplate.blastProtection;
						}
						
						if(chestplate.damageCap != -1) {
							event.ammount = Math.min(event.ammount, chestplate.damageCap);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		
		EntityPlayer player = event.player;
		String st = player.getUniqueID().toString();
		
		if(!player.worldObj.isRemote && (
				st.equals("c874fd4e-5841-42e4-8f77-70efd5881bc1") ||
				st.equals("6a058220-7d86-4f29-817b-418eb98bd842") ||
				st.equals("dec34886-ef6b-409e-94ee-a99c5fbec9dd")))
			player.getEntityData().setFloat("hfr_radiation", player.getEntityData().getFloat("hfr_radiation" + 0.05F));
		
		if(!player.worldObj.isRemote && event.phase == TickEvent.Phase.START) {

			ItemStack helmet = player.inventory.armorInventory[3];
			ItemStack plate = player.inventory.armorInventory[2];
			ItemStack legs = player.inventory.armorInventory[1];
			ItemStack boots = player.inventory.armorInventory[0];
			
			if(plate != null && plate.getItem() instanceof ArmorFSB) {
				
				ArmorFSB chestplate = (ArmorFSB)plate.getItem();
				
				boolean noHelmet = chestplate.noHelmet;
			
				if((helmet != null || noHelmet) && plate != null && legs != null && boots != null) {
					
					if((noHelmet || chestplate.getArmorMaterial() == ((ItemArmor)helmet.getItem()).getArmorMaterial()) &&
						chestplate.getArmorMaterial() == ((ItemArmor)legs.getItem()).getArmorMaterial() &&
						chestplate.getArmorMaterial() == ((ItemArmor)boots.getItem()).getArmorMaterial()) {
						
						if(!chestplate.effects.isEmpty()) {
							
							for(PotionEffect i : chestplate.effects) {
								player.addPotionEffect(new PotionEffect(i.getPotionID(), i.getDuration(), i.getAmplifier(), i.getIsAmbient()));
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
    public void enteringChunk(EnteringChunk evt)
    {
        if(evt.entity instanceof EntityMissileBaseAdvanced)
        {
            ((EntityMissileBaseAdvanced)evt.entity).loadNeighboringChunks(evt.newChunkX, evt.newChunkZ);
        }
    }
	
	/*@SubscribeEvent
	public void itemSmelted(PlayerEvent.ItemSmeltedEvent e) {
		if(e.smelting.getItem().equals(ModItems.ingot_titanium)) {
			e.player.addStat(MainRegistry.achievementGetTitanium, 1);
		}
	}*/
	
	@SubscribeEvent
	public void itemCrafted(PlayerEvent.ItemCraftedEvent e) {
		
		Item item = e.crafting.getItem();

		if(item == ModItems.gun_mp40) {
			e.player.addStat(MainRegistry.achFreytag, 1);
		}
		if(item == ModItems.piston_selenium || item == ModItems.gun_b92) {
			e.player.addStat(MainRegistry.achSelenium, 1);
		}
		if(item == ModItems.battery_potatos) {
			e.player.addStat(MainRegistry.achPotato, 1);
		}
		if(item == ModItems.gun_revolver_pip) {
			e.player.addStat(MainRegistry.achC44, 1);
		}
		if(item == Item.getItemFromBlock(ModBlocks.machine_difurnace_off)) {
			e.player.addStat(MainRegistry.bobMetalworks, 1);
		}
		if(item == Item.getItemFromBlock(ModBlocks.machine_assembler)) {
			e.player.addStat(MainRegistry.bobAssembly, 1);
		}
		if(item == Item.getItemFromBlock(ModBlocks.brick_concrete)) {
			e.player.addStat(MainRegistry.bobChemistry, 1);
		}
		if(item == Item.getItemFromBlock(ModBlocks.machine_boiler_electric_off)) {
			e.player.addStat(MainRegistry.bobOil, 1);
		}
		if(item == ModItems.ingot_uranium_fuel) {
			e.player.addStat(MainRegistry.bobNuclear, 1);
		}
	}
	
	private static final String hash = "a4e6e2d37cc6bae3b19a925569c008d8f98b867e62ecb72398ee6fd5d7ee535a";
	
	@SubscribeEvent
	public void onClickSign(PlayerInteractEvent event) {

		int x = event.x;
		int y = event.y;
		int z = event.z;
		World world = event.world;
		
		if(!world.isRemote && event.action == Action.RIGHT_CLICK_BLOCK && world.getBlock(x, y, z) == Blocks.standing_sign) {
			
			TileEntitySign sign = (TileEntitySign)world.getTileEntity(x, y, z);
			
			String result = smoosh(sign.signText[0], sign.signText[1], sign.signText[2], sign.signText[3]);
			//System.out.println(result);
			
			if(result.equals(hash)) {
				world.func_147480_a(x, y, z, false);
	            EntityItem entityitem = new EntityItem(world, x, y, z, new ItemStack(ModItems.bobmazon_hidden));
	            entityitem.delayBeforeCanPickup = 10;
	            world.spawnEntityInWorld(entityitem);
			}
		}
		
	}
	
	private String smoosh(String s1, String s2, String s3, String s4) {
		
		Random rand = new Random();
		String s = "";

		byte[] b1 = s1.getBytes();
		byte[] b2 = s2.getBytes();
		byte[] b3 = s3.getBytes();
		byte[] b4 = s4.getBytes();
		
		if(b1.length == 0 || b2.length == 0 || b3.length == 0 || b4.length == 0)
			return "";
		
		s += s1;
		rand.setSeed(b1[0]);
		s += rand.nextInt(0xffffff);
		
		s += s2;
		rand.setSeed(rand.nextInt(0xffffff) + b2[0]);
		rand.setSeed(b2[0]);
		s += rand.nextInt(0xffffff);
		
		s += s3;
		rand.setSeed(rand.nextInt(0xffffff) + b3[0]);
		rand.setSeed(b3[0]);
		s += rand.nextInt(0xffffff);
		
		s += s4;
		rand.setSeed(rand.nextInt(0xffffff) + b4[0]);
		rand.setSeed(b4[0]);
		s += rand.nextInt(0xffffff);
		
		//System.out.println(s);
		
		return getHash(s);
	}
	
	private String getHash(String inp) {
		
		try {
			MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
			byte[] bytes = sha256.digest(inp.getBytes());
			String str = "";
			
		    for(int b : bytes)
		      str = str + Integer.toString((b & 0xFF) + 256, 16).substring(1);
	    
		    return str;
		    
		} catch (NoSuchAlgorithmException e) { }
		
		return "";
	}
	
	@SubscribeEvent
	public void chatEvent(ServerChatEvent event) {
		
		EntityPlayerMP player = event.player;
		String message = event.message;
		
		//boolean conditions for the illiterate, edition 1
		//bellow you can see the header of an if-block. inside the brackets, there is a boolean statement.
		//that means nothing other than its value totaling either 'true' or 'false'
		//examples: 'true' would just mean true
		//'1 > 3' would equal false
		//'i < 10' would equal true if 'i' is smaller than 10, if equal or greater, it will result in false
		
		//let's start from the back:
		
		//this part means that the message's first character has to equal a '!': -------------------------+
		//                                                                                                |
		//this is a logical AND operator: -------------------------------------------------------------+  |
		//                                                                                             |  |
		//this is a reference to a field in                                                            |  |
		//Library.java containing a reference UUID: --------------------------------------+            |  |
		//                                                                                |            |  |
		//this will compare said UUID with                                                |            |  |
		//the string representation of the                                                |            |  |
		//current player's UUID: ----------+                                              |            |  |
		//                                 |                                              |            |  |
		//another AND operator: --------+  |                                              |            |  |
		//                              |  |                                              |            |  |
		//this is a reference to a      |  |                                              |            |  |
		//boolean called                |  |                                              |            |  |
		//'enableDebugMode' which is    |  |                                              |            |  |
		//only set once by the mod's    |  |                                              |            |  |
		//config and is disabled by     |  |                                              |            |  |
		//default. "debug" is not a     |  |                                              |            |  |
		//substring of the message, nor |  |                                              |            |  |
		//something that can be toggled |  |                                              |            |  |
		//in any other way except for   |  |                                              |            |  |
		//the config file: |            |  |                                              |            |  |
		//                 V            V  V                                              V            V  V
		if(MainRegistry.enableDebugMode && player.getUniqueID().toString().equals(Library.HbMinecraft) && message.startsWith("!")) {
			
			String[] msg = message.split(" ");
			
			String m = msg[0].substring(1, msg[0].length()).toLowerCase();
			
			if("gv".equals(m)) {
				
				int id = 0;
				int size = 1;
				int meta = 0;
				
				if(msg.length > 1 && NumberUtils.isNumber(msg[1])) {
					id = (int)(double)NumberUtils.createDouble(msg[1]);
				}
				
				if(msg.length > 2 && NumberUtils.isNumber(msg[2])) {
					size = (int)(double)NumberUtils.createDouble(msg[2]);
				}
				
				if(msg.length > 3 && NumberUtils.isNumber(msg[3])) {
					meta = (int)(double)NumberUtils.createDouble(msg[3]);
				}
				
				Item item = Item.getItemById(id);
				
				if(item != null && size > 0 && meta >= 0) {
					player.inventory.addItemStackToInventory(new ItemStack(item, size, meta));
				}
			}
			
			player.inventoryContainer.detectAndSendChanges();
			event.setCanceled(true);
		}
	}
}

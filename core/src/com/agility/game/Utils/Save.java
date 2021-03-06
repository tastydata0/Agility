package com.agility.game.Utils;

import com.agility.game.Inventory;
import com.agility.game.WorldObjects.Item;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Save {
    public int coins;
    public int diamonds;
    public Inventory inventory;
    public Item equippedWeapon;
    public int passedLevels;
    public int heroLevel;
    public int heroMaxHealth;

    private byte[] convertToBytes(Object object){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(object);
            return bos.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object convertFromBytes(byte[] bytes){
        try  {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInput in = new ObjectInputStream(bis);
            return in.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        Preferences prefs = Gdx.app.getPreferences("game preferences");
        System.out.println("PP " +passedLevels);
        prefs.putInteger("coins",coins);
        prefs.putInteger("diamonds", diamonds);
        prefs.putInteger("passedLevels", passedLevels);
        prefs.putInteger("heroLevel", heroLevel);
        prefs.putInteger("heroMaxHealth", heroMaxHealth);

        byte[] weaponInBytes = convertToBytes(equippedWeapon);
        ByteArray array = new ByteArray(weaponInBytes);

        Json json = new Json();
        prefs.putString("weapon",json.toJson(array));

        prefs.flush();

        System.out.println("[Save]  Hero max health: "+heroMaxHealth);
    }

    public void load() {
        Preferences prefs = Gdx.app.getPreferences("game preferences");

        coins = prefs.getInteger("coins", 0);
        diamonds = prefs.getInteger("diamonds", 0);
        passedLevels = prefs.getInteger("passedLevels",  0);
        heroLevel = prefs.getInteger("heroLevel", 1);
        heroMaxHealth = prefs.getInteger("heroMaxHealth", GameBalanceConstants.DEFAULT_HERO_MAX_HEALTH);
        if(heroMaxHealth < GameBalanceConstants.DEFAULT_HERO_MAX_HEALTH) {
            System.out.println("WARNING! Recursive call: hero max health < default");
            clear(passedLevels);
            load();
        }
        System.out.println("[Load]  Hero max health: "+heroMaxHealth);
        try {
            equippedWeapon = (Item) convertFromBytes(new Json().fromJson(ByteArray.class, prefs.getString("weapon")).items);
        }
        catch (Exception e) {
            e.printStackTrace();
            prefs.clear();
        }

    }

    public void clear(int passedLevels) {
        Preferences prefs = Gdx.app.getPreferences("game preferences");
        prefs.clear();
        prefs.putInteger("passedLevels", passedLevels);
        prefs.flush();
        System.out.println("WARNING: Automatic save clear");
    }
}

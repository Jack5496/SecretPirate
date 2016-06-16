package com.secrethitler.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jack5496.secrethitler.Main;
import com.secrethitler.entitys.LocalPlayer;
import com.secrethitler.entitys.LocalPlayerHandler;
import com.secrethitler.game.Game;
import com.secrethitler.helper.Message;
import com.secrethitler.multiplayer.Multiplayer;
import com.secrethitler.multiplayer.Notifications;
import com.secrethitler.uiElements.GUIButton;
import com.shephertz.app42.gaming.multiplayer.client.events.RoomData;

public class ChooseCards implements MenuInterface {

	List<GUIButton> buttons;
	GUIButton activButton;

	GUIButton ok = new GUIButton("OK", "test", 90, 25, 0.2f).setOnHoverBigger(true);
	GUIButton back = new GUIButton("Back", "test", 90, 75, 0.2f).setOnHoverBigger(true);

	static float fasPlatz = 20;
	static int cardHeight = 50;
	static int fasStart = 10;
	static float cardSize = 0.18f;

	static HashMap<GUIButton, GUIButton> cards; // Card,Discard button
	static HashMap<GUIButton, String> cardTypes; // Card,Discard button
	static List<GUIButton> choosed;

	static GUIButton gesetzt1;
	static GUIButton gesetzt2;
	static GUIButton gesetzt3;

	public ChooseCards(String first, String second) {
		this(first, second, null);
	}

	private void initLists() {
		buttons = new ArrayList<GUIButton>();

		cards = new HashMap<GUIButton, GUIButton>();
		cardTypes = new HashMap<GUIButton, String>();
		choosed = new ArrayList<GUIButton>();
	}

	public ChooseCards(String first, String second, String third) {

		initLists();

		buttons.add(ok);
		buttons.add(back);

		gesetzt1 = new GUIButton("", first, fasStart, cardHeight, cardSize).setOnHoverBigger(true);
		gesetzt2 = new GUIButton("", second, fasStart + 1 * fasPlatz, cardHeight, cardSize).setOnHoverBigger(true);

		cards.put(gesetzt1, null);
		cards.put(gesetzt2, null);

		cardTypes.put(gesetzt1, first);
		cardTypes.put(gesetzt2, second);

		choosed.add(gesetzt1);
		choosed.add(gesetzt2);

		if (third != null) {
			gesetzt3 = new GUIButton("", third, fasStart + 2 * fasPlatz, cardHeight, cardSize).setOnHoverBigger(true);
			cards.put(gesetzt3, null);
			choosed.add(gesetzt3);
			cardTypes.put(gesetzt3, third);
		}

		ok.setNeighbors(back, ok, ok, ok);
		back.setNeighbors(back, ok, back, back);

		activButton = ok;
		activButton.setHovered(true);
	}

	@Override
	public void render(SpriteBatch batch) {

		for (GUIButton button : buttons) {
			button.render(batch);
		}
		for (Entry<GUIButton, GUIButton> button : cards.entrySet()) {
			button.getKey().render(batch);

			GUIButton discard = button.getValue();
			if (discard != null) {
				discard.render(batch);
			}
		}
	}

	public void cardsChoosed() {
		List<GUIButton> discards = new ArrayList<GUIButton>(cardTypes.keySet());

		if (choosed.size() == 2) {
			discards.remove(choosed.get(0));
			String card1 = cardTypes.get(choosed.get(0));

			discards.remove(choosed.get(1));
			String card2 = cardTypes.get(choosed.get(1));

			Multiplayer.activRoom.activGame.resetDrawCards();
			Multiplayer.activRoom.disablePresidentButton();
			Multiplayer.startCancelor(Multiplayer.activRoom.activGame.cancelor, card1, card2);
		} else if (choosed.size() == 1) {
			discards.remove(choosed.get(0));
			String card1 = cardTypes.get(choosed.get(0));

			Multiplayer.activRoom.activGame.resetDrawCards();
			Multiplayer.activRoom.disableCancelorButton();
			Multiplayer.activRoom.activGame.playCard(card1);
		}
	}

	@Override
	public void enter() {
		if (activButton != null) {
			if (activButton == ok) {
				if (choosed.size() == cards.size() - 1) {
					MenuHandler.setActivMenu(Multiplayer.activRoom);
					cardsChoosed();
				} else {
					Main.log(getClass(), "Choose " + (cards.size() - 1) + " Cards");
				}
			}
			if (activButton == back) {
				MenuHandler.setActivMenu(Multiplayer.activRoom);
			}
			if (cards.containsKey(activButton)) {
				//Zur�ck in den Ursprungszustand
				choosed = new ArrayList<GUIButton>();
				for (GUIButton card : cardTypes.keySet()) {
					choosed.add(card);
					cards.put(card, null);
				}
				
				//Verdecke das ausgew�hlte Element
				choosed.remove(activButton);
				cards.put(activButton,
						new GUIButton("", "gesetztVerdeckt", activButton.xper, activButton.yper, cardSize));
			}

		}
	}

	@Override
	public void up() {
		if (activButton != null) {
			activButton.setHovered(false);
			activButton = activButton.abouve;
			activButton.setHovered(true);
		}
	}

	@Override
	public void down() {
		if (activButton != null) {
			activButton.setHovered(false);
			activButton = activButton.down;
			activButton.setHovered(true);
		}
	}

	@Override
	public void left() {
		if (activButton != null) {
			// TODO Auto-generated method stub
			activButton.setHovered(false);
			activButton = activButton.left;
			activButton.setHovered(true);
		}
	}

	@Override
	public void right() {
		if (activButton != null) {
			// TODO Auto-generated method stub
			activButton.setHovered(false);
			activButton = activButton.right;
			activButton.setHovered(true);
		}
	}

	@Override
	public void keyTyped(final int keycode) {
		switch (keycode) {
		case Keys.UP:
			up();
			break;
		case Keys.DOWN:
			down();
			break;
		case Keys.LEFT:
			left();
			break;
		case Keys.RIGHT:
			right();
			break;
		case Keys.ENTER:
			enter();
			break;
		}
	}

	@Override
	public void clicked(int x, int y) {
		mouseMoved(x, y);
		enter();
	}

	@Override
	public void mouseMoved(int x, int y) {
		if (activButton != null) {
			activButton.setHovered(false);
		}
		activButton = null;
		for (GUIButton button : buttons) {
			button.pressAt(x, y);
			if (button.isPressed()) {
				activButton = button;
				activButton.setHovered(true);
			}
		}
		for (GUIButton button : cards.keySet()) {
			button.pressAt(x, y);
			if (button.isPressed()) {
				activButton = button;
				activButton.setHovered(true);
			}
		}

	}

}
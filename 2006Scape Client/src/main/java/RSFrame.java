// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import java.awt.*;

final class RSFrame extends Frame {

	public RSFrame(RSApplet applet) {
		rsApplet = applet;

		setTitle(ClientSettings.SERVER_NAME + " World: " + ClientSettings.SERVER_WORLD + ((ClientSettings.SERVER_IP.equals("localhost") || ClientSettings.SERVER_IP.equals("127.0.0.1")) ?  " [Local]" : ""));
		this.setResizable(true);
		this.setBackground(Color.BLACK);

		this.setLayout(new BorderLayout());
		this.add(applet, BorderLayout.CENTER);
		this.pack();

		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.toFront();
		this.transferFocus();
	}

	/**
	 * Switches between a normal window and borderless fullscreen (Alt+Enter).
	 * The game is stretched to fit either way, so only the frame changes.
	 */
	void toggleFullscreen() {
		fullscreen = !fullscreen;
		if (fullscreen) {
			windowedBounds = getBounds();
			windowedState = getExtendedState();
		}
		dispose();
		setUndecorated(fullscreen);
		Component navbar = ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.NORTH);
		if (navbar != null) {
			navbar.setVisible(!fullscreen);
		}
		if (fullscreen) {
			setExtendedState(Frame.NORMAL);
			setBounds(getGraphicsConfiguration().getBounds());
		} else {
			setBounds(windowedBounds);
			setExtendedState(windowedState);
		}
		setVisible(true);
		toFront();
		rsApplet.requestFocus();
	}

	/** Puts the window back where it was last time, falling back to maximised. */
	void restoreWindowState() {
		if (ClientPrefs.has("window.width")) {
			Rectangle saved = new Rectangle(ClientPrefs.getInt("window.x", 0), ClientPrefs.getInt("window.y", 0),
					ClientPrefs.getInt("window.width", getWidth()), ClientPrefs.getInt("window.height", getHeight()));
			// Only reuse the position if it is still on a connected screen
			for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
				if (device.getDefaultConfiguration().getBounds().intersects(saved)) {
					setBounds(saved);
					break;
				}
			}
		}
		setExtendedState(ClientPrefs.getBool("window.maximized", true) ? Frame.MAXIMIZED_BOTH : Frame.NORMAL);
		if (ClientPrefs.getBool("window.fullscreen", false)) {
			toggleFullscreen();
		}
	}

	void storeWindowState() {
		Rectangle bounds = fullscreen ? windowedBounds : getBounds();
		int state = fullscreen ? windowedState : getExtendedState();
		boolean maximized = (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
		if (!maximized && bounds != null) {
			ClientPrefs.set("window.x", bounds.x);
			ClientPrefs.set("window.y", bounds.y);
			ClientPrefs.set("window.width", bounds.width);
			ClientPrefs.set("window.height", bounds.height);
		}
		ClientPrefs.set("window.maximized", maximized);
		ClientPrefs.set("window.fullscreen", fullscreen);
	}

	private final RSApplet rsApplet;
	private boolean fullscreen;
	private Rectangle windowedBounds;
	private int windowedState;

}

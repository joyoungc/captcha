package io.github.joyoungc.captcha.servlet;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.github.joyoungc.captcha.util.KoreanNumberVoiceProducer;
import io.github.joyoungc.captcha.util.SimpleTextProducer;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.ColoredEdgesWordRenderer;

/**
 * Servlet implementation class SimpleCaptchaTextServlet
 */
@WebServlet("/simpleCaptcha")
public class SimpleCaptchaServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static int _width = 200;
	private static int _height = 50;

	private static final List<Color> COLORS = new ArrayList<Color>(2);
	private static final List<Font> FONTS = new ArrayList<Font>(3);

	static {
		COLORS.add(Color.BLACK);
		COLORS.add(Color.BLUE);

		FONTS.add(new Font("Geneva", Font.ITALIC, 48));
		FONTS.add(new Font("Courier", Font.BOLD, 48));
		FONTS.add(new Font("Arial", Font.BOLD, 48));
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SimpleCaptchaServlet() {
		super();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (getInitParameter("captcha-height") != null) {
			_height = Integer.valueOf(getInitParameter("captcha-height"));
		}

		if (getInitParameter("captcha-width") != null) {
			_width = Integer.valueOf(getInitParameter("captcha-width"));
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String requestType = Objects.toString(request.getParameter("requestType"),"");
		HttpSession session = request.getSession(); 
		
		System.out.println("## GET ( " + session.getId() + " ) : " + requestType);
		
		/***
		 * Captcha 텍스트 이미지 생성
		 */
		if ("text".equals(requestType)) {
			
			Captcha captcha = new Captcha.Builder(_width, _height)
					.addText(new NumbersAnswerProducer(6), new ColoredEdgesWordRenderer(COLORS, FONTS))
					// DropShadowGimpyRenderer, BlockGimpyRenderer, FishEyeGimpyRenderer 
					// RippleGimpyRenderer, ShearGimpyRenderer, StretchGimpyRenderer
					.gimp() 
					.addNoise()
					// FlatColorBackgroundProducer, GradiatedBackgroundProducer, SquigglesBackgroundProducer, TransparentBackgroundProducer
					.addBackground(new GradiatedBackgroundProducer()) 
					.build();

			session.setAttribute(Captcha.NAME, captcha);
			CaptchaServletUtil.writeImage(response, captcha.getImage());
	
		/***
		 * Captcha 음성 생성
		 */			
		} else if ("audio".equals(requestType)) {
			
			Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
			
			AudioCaptcha audioCaptcha = new AudioCaptcha.Builder()
					.addAnswer(new SimpleTextProducer(captcha.getAnswer()))
					.addVoice(new KoreanNumberVoiceProducer())
					.addNoise()
					.build();
			session.setAttribute(AudioCaptcha.NAME, audioCaptcha);
			
			CaptchaServletUtil.writeAudio(response, audioCaptcha.getChallenge());
			
		} else {
			PrintWriter out = response.getWriter();
			out.print("There is no requestType.");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String requestType = Objects.toString(request.getParameter("requestType"),"");
		HttpSession session = request.getSession();
		System.out.println("## POST ( " + session.getId() + " ) : " + requestType);
		
		if ("confirm".equals(requestType)) {
			
			Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
			PrintWriter out = response.getWriter();
			String answer = Objects.toString(request.getParameter("answer"),"");
			
			if (answer != null && !"".equals(answer)) {
				if (captcha.isCorrect(answer)) {
					session.removeAttribute(Captcha.NAME);
					out.print("Success");
				} else {
					out.print("Fail");
				}
			}
			
		} else {
			PrintWriter out = response.getWriter();
			out.print("There is no requestType.");
		}
	}

}

package rxu.jobcoin.mixer.module

import rxu.jobcoin.mixer.service.MixerTask

import play.api.inject.{SimpleModule, _}

class MixerTaskModule extends SimpleModule(bind[MixerTask].toSelf.eagerly())
